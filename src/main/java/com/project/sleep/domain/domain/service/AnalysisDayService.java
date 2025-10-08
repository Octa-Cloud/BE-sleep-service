package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.domain.repository.DailyReportRepository;
import com.project.sleep.global.exception.RestApiException;
import com.project.sleep.global.exception.code.status.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AnalysisDayService {

    private final DailyReportRepository dailyReportRepository;

    public DailyReport findByUserNoAndDate(Long userNo, LocalDate date) {
        return dailyReportRepository.findOneByUserNoAndSleepDateRange(userNo, date, date.plusDays(1))
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

    }
}
