package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.DailyReportResponse;
import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.domain.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class GetDailyReportUseCase {

    private final DailyReportService dailyReportService;

    @Cacheable(value = "dailyReport", key = "#userNo + '_' + #date")
    public DailyReportResponse execute(Long userNo, LocalDate date) {
        DailyReport entity = dailyReportService.findByUserNoAndDate(userNo, date);
        return DailyReportResponse.from(entity);
    }
}
