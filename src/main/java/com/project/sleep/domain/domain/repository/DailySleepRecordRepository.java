package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;

public interface DailySleepRecordRepository extends MongoRepository<DailySleepRecord, String> {
    // 요청된 날짜(date)보다 작거나 같은 날짜들 중, 가장 최근 8개 데이터를 내림차순으로 조회
    List<DailySleepRecord> findTop8BySleepDateLessThanEqualOrderBySleepDateDesc(LocalDate date);
    DailySleepRecord findBySleepDate(LocalDate date);
}