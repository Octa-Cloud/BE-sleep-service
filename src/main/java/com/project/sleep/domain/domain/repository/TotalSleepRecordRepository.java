package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.TotalSleepRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TotalSleepRecordRepository extends MongoRepository<TotalSleepRecord, String> {
    Optional<TotalSleepRecord> findByUserNo(Long userNo);
}
