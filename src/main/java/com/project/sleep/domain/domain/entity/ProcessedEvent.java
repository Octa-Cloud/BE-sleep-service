package com.project.sleep.domain.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * 카프카 이벤트(eventId)를 PK로 멱등 처리 상태를 추적하는 컬렉션.
 * IN_PROGRESS → SUCCESS | ERROR(+attempts++) 로 전이.
 * - user.delete.command / user.delete.compensate 리스너가 함께 사용.
 */
@Document("processed_event")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProcessedEvent {
    @Id
    private String eventId;            // 멱등키(카프카 메시지의 eventId)

    private String type;               // DELETE | COMPENSATE
    private String status;             // IN_PROGRESS | SUCCESS | ERROR
    private Integer attempts;          // 재시도 횟수
    private String lastError;          // 마지막 오류 메시지(옵션)
    private Instant updatedAt;         // 상태 갱신 시각
}