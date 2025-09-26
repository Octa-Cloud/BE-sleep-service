package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.DailyReport;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.Optional;

public interface DailyReportRepository extends MongoRepository<DailyReport, String> {
    Optional<DailyReport> findOneByUserNoAndSleepDate(Long userNo, Date sleepDate);
}
