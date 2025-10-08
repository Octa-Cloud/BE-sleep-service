package com.project.sleep.domain.infra.repository;

import com.project.sleep.domain.domain.entity.PeriodicReport;

import java.time.LocalDate;
import java.util.Optional;

public interface PeriodicReportCustomRepository {

    Optional<PeriodicReport> findOneByUserNoAndTypeAndDateBetween(
            Long userNo, PeriodicReport.Type type, LocalDate startInclusive, LocalDate endInclusive
    );
}
