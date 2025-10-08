package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.infra.repository.DailyReportCustomRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

public interface DailyReportRepository extends MongoRepository<DailyReport, String>, DailyReportCustomRepository {
    Optional<DailyReport> findByUserNoAndSleepDate(Long userNo, LocalDate date);
}
