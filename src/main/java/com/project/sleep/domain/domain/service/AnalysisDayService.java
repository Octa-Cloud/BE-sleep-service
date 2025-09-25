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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AnalysisDayService {

    private final DailyReportRepository dailyReportRepository;

    public DailyReport findByUserNoAndDate(Long userNo, LocalDate date) {

        // date가 null 이면 오늘 기준으로 조회
       // LocalDate targetDate = (date != null) ? date : LocalDate.now();

        LocalDate targetDate = (date != null)
                ? date
                : LocalDate.now(ZoneId.of("Asia/Seoul"));

        // Mongo에 저장된 값이 UTC 자정(…T00:00:00Z)라면, 동일하게 UTC 자정으로 변환
        Date key = Date.from(targetDate.atStartOfDay(ZoneOffset.UTC).toInstant());

        return dailyReportRepository
                .findOneByUserNoAndSleepDate(userNo, key)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

    }
}
