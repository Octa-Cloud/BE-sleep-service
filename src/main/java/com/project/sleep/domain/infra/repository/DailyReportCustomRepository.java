package com.project.sleep.domain.infra.repository;

import com.project.sleep.domain.domain.entity.DailyReport;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyReportCustomRepository {
    Optional<DailyReport> findOneByUserNoAndSleepDateRange(
            Long userNo, LocalDate startInclusive, LocalDate endExclusive);
}