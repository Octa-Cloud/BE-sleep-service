package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.PeriodicReport;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface PeriodicReportRepository extends MongoRepository<PeriodicReport, String> {
    PeriodicReport findByUserNoAndTypeAndDateBetween(
            Long userNo, String type, LocalDate start, LocalDate end);
}
