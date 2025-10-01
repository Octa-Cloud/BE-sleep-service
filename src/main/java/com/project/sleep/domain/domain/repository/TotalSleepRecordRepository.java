package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.TotalSleepRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TotalSleepRecordRepository extends MongoRepository<TotalSleepRecord, Long> {
}
