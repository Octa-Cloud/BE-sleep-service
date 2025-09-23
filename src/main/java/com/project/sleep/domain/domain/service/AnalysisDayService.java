package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.application.dto.response.AnalysisDayResponse;
import com.project.sleep.domain.application.usecase.AnalysisDayUseCase;
import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.domain.repository.DailyReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.project.sleep.domain.application.dto.response.AnalysisDayResponse.mapToResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalysisDayService implements AnalysisDayUseCase {

    private final DailyReportRepository dailyReportRepository;

    @Override
    public AnalysisDayResponse getDailyAnalysis(Long userId, LocalDate date) {

        // date가 null 이면 오늘 기준으로 조회
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        // 몽고디비 userNo 가 String 타입이므로 타입변환
        String userNo = String.valueOf(userId);

        DailyReport report = dailyReportRepository
                .findByUserNoAndSleepDate(userNo,targetDate)
                .orElseThrow(()-> new IllegalStateException("Daily report not found"));


        return mapToResponse(report);

    }
}
