package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailySleepRecordRepository extends MongoRepository<DailySleepRecord, String> {
    List<DailySleepRecord> findByUserNoOrderBySleepDateDesc(String userNo);
    DailySleepRecord findByUserNoAndSleepDate(String userNo, LocalDate sleepDate);
}
