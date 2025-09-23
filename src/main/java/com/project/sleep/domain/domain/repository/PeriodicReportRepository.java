package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.PeriodicReport;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface PeriodicReportRepository extends MongoRepository<PeriodicReport, String> {
    List<PeriodicReport> findByUserNoAndTypeAndDateBetween(
            String userNo, String type, LocalDate start, LocalDate end);
}
