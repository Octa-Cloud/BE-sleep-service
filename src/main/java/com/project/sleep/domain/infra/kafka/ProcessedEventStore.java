package com.project.sleep.domain.infra.kafka;

import com.project.sleep.domain.domain.entity.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ProcessedEventStore {
    /**
     * MongoDB 컬렉션(processed_event)의 상태 관리 유틸리티.
     *
     * 역할
     * - 이벤트(eventId=PK)의 처리 상태(IN_PROGRESS / SUCCESS / ERROR / TERMINAL_FAIL)를 일관되게 관리.
     * - 중복 처리 방지(멱등성 보장)를 위한 tryBegin() 제공.
     * - 기술적 장애, 비즈니스 실패, DLT 등 다양한 상황에서 상태 갱신 로직을 공통화.
     *
     * 특징
     * - 모든 컨슈머(UserDeletionConsumers, DLT 리스너 등)가 공통 사용.
     * - DLT에서 eventId만 넘어오는 경우를 대비해 TERMINAL_FAIL은 upsert 방식으로 처리.
     * - processed_event 컬렉션 이름은 고정("processed_event")으로 통일.
     */
    private final MongoTemplate mongo;
    private static final String COLL = "processed_event";

    /** 처음 처리(IN_PROGRESS) 시도. 이미 완료/터미널이면 false 반환(스킵). */
    public boolean tryBegin(String eventId, String type) {
        try {
            mongo.insert(ProcessedEvent.builder()
                    .eventId(eventId)
                    .type(type)
                    .status("IN_PROGRESS")
                    .attempts(1)
                    .updatedAt(Instant.now())
                    .build(), COLL);
            return true;
        } catch (DuplicateKeyException e) {
            var existing = mongo.findById(eventId, ProcessedEvent.class, COLL);
            return existing == null ||
                    (!"SUCCESS".equals(existing.getStatus()) &&
                            !"TERMINAL_FAIL".equals(existing.getStatus()));
        }
    }

    public void markSuccess(String eventId) {
        var pe = mongo.findById(eventId, ProcessedEvent.class, COLL);
        if (pe != null) {
            pe.setStatus("SUCCESS");
            pe.setUpdatedAt(Instant.now());
            mongo.save(pe, COLL);
        }
    }

    public void markError(String eventId, String msg) {
        var pe = mongo.findById(eventId, ProcessedEvent.class, COLL);
        if (pe != null) {
            pe.setStatus("ERROR");
            pe.setAttempts((pe.getAttempts() == null ? 0 : pe.getAttempts()) + 1);
            pe.setLastError(msg);
            pe.setUpdatedAt(Instant.now());
            mongo.save(pe, COLL);
        }
    }

    /** DLT 등 eventId만 넘어온 상황을 고려해 upsert 형태로 터미널 마킹 */
    public void markTerminalFailUpsert(String eventId, String typeIfCreate, String msg) {
        var pe = mongo.findById(eventId, ProcessedEvent.class, COLL);
        if (pe == null) {
            pe = ProcessedEvent.builder()
                    .eventId(eventId)
                    .type(typeIfCreate)
                    .status("TERMINAL_FAIL")
                    .attempts(1)
                    .lastError(msg)
                    .updatedAt(Instant.now())
                    .build();
        } else {
            pe.setStatus("TERMINAL_FAIL");
            pe.setAttempts((pe.getAttempts() == null ? 0 : pe.getAttempts()) + 1);
            pe.setLastError(msg);
            pe.setUpdatedAt(Instant.now());
        }
        mongo.save(pe, COLL);
    }
}