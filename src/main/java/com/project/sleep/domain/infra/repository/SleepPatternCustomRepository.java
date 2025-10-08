package com.project.sleep.domain.infra.repository;

import com.project.sleep.domain.domain.entity.DailySleepRecord;

import java.time.LocalDate;
import java.util.List;

public interface SleepPatternCustomRepository {
    List<DailySleepRecord> findByUserNoAndSleepDateBetween(
            Long userNo, LocalDate startInclusive, LocalDate endInclusive
    );
}