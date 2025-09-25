package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // Optional 임포트 추가

public interface DailySleepRecordRepository extends MongoRepository<DailySleepRecord, String> {
    // 요청된 날짜(date)보다 작거나 같은 날짜들 중, 가장 최근 8개 데이터를 내림차순으로 조회
    List<DailySleepRecord> findTop8BySleepDateLessThanEqualOrderBySleepDateDesc(LocalDate date);
    DailySleepRecord findBySleepDate(LocalDate date);

    // 특정 사용자의 최근 8개 수면 기록 조회
    List<DailySleepRecord> findTop8ByUserNoOrderBySleepDateDesc(Long userNo);

    // 특정 사용자의 특정 날짜 수면 기록을 Optional로 조회
    Optional<DailySleepRecord> findByUserNoAndSleepDate(Long userNo, LocalDate sleepDate);
}
