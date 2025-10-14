package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.domain.domain.entity.PeriodicReport;
import com.project.sleep.domain.domain.service.PeriodicReportService;
import com.project.sleep.global.util.DateConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetPeriodicReportUseCase {
    private final PeriodicReportService periodicReportService;

    @Cacheable(value = "weeklyReport", key = "#userNo + ':' + #date")
    public PeriodicReportResponse getWeeklyReport(Long userNo, LocalDate date){
        LocalDate start = DateConvertor.weekStart(date);
        LocalDate end = DateConvertor.weekEnd(date);

        return periodicReportService.getReport(PeriodicReport.Type.WEEKLY, userNo, start, end)
                .map(PeriodicReportResponse::mapToResponse)
                .orElse(PeriodicReportResponse.emptyResponse());
    }

    @Cacheable(value = "monthlyReport", key = "#userNo + ':' + #date")
    public PeriodicReportResponse getMonthlyReport(Long userNo, LocalDate date){
        LocalDate start = DateConvertor.monthStart(date);
        LocalDate end = DateConvertor.monthEndInclusive(date);

        return periodicReportService.getReport(PeriodicReport.Type.MONTHLY, userNo, start, end)
                .map(PeriodicReportResponse::mapToResponse)
                .orElse(PeriodicReportResponse.emptyResponse());
    }
}
