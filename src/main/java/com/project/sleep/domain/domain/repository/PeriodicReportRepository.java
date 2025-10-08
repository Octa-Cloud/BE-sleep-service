package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.PeriodicReport;
import com.project.sleep.domain.infra.repository.PeriodicReportCustomRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PeriodicReportRepository extends MongoRepository<PeriodicReport, String>, PeriodicReportCustomRepository {
}