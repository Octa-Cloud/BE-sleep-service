package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.application.dto.response.AnalysisDayResponse;
import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.domain.repository.DailyReportRepository;
import com.project.sleep.global.exception.RestApiException;
import com.project.sleep.global.exception.code.status.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AnalysisDayService {

    private final DailyReportRepository dailyReportRepository;

    public AnalysisDayResponse getDailyAnalysis(Long userId, LocalDate date) {

        // date가 null 이면 오늘 기준으로 조회
        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        // UTC 자정 기준 하루 범위
        ZonedDateTime startUtc = targetDate.atStartOfDay(ZoneId.of("UTC"));
        ZonedDateTime endUtc   = startUtc.plusDays(1);

        Date from = Date.from(startUtc.toInstant());
        Date to   = Date.from(endUtc.toInstant());

        DailyReport report = dailyReportRepository
                .findOneByUserNoAndSleepDateBetween(userId, from, to)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        return AnalysisDayResponse.from(report);
    }
}
