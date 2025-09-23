package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;

public interface SleepRecordRepository extends MongoRepository<DailySleepRecord, String> { // db와 소통

    List<DailySleepRecord> findAllBySleepDateBetween(LocalDate startDate, LocalDate endDate);

}