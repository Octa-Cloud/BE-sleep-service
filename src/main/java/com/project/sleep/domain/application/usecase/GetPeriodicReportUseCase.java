package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.domain.domain.entity.PeriodicReport;
import com.project.sleep.domain.domain.service.PeriodicReportService;
import com.project.sleep.global.util.DateConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetPeriodicReportUseCase {
    private final PeriodicReportService periodicReportService;

    public PeriodicReportResponse getWeeklyReport(PeriodicReport.Type type, Long userNo, LocalDate date){
        LocalDate start = DateConvertor.weekStart(date);
        LocalDate end = DateConvertor.weekEnd(date);

        return periodicReportService.getReport(type, userNo, start, end)
                .map(PeriodicReportResponse::mapToResponse)
                .orElse(PeriodicReportResponse.emptyResponse());
    }

    public PeriodicReportResponse getMonthlyReport(PeriodicReport.Type type, Long userNo, LocalDate date){
        LocalDate start = DateConvertor.monthStart(date);
        LocalDate end = DateConvertor.monthEndInclusive(date);

        return periodicReportService.getReport(type, userNo, start, end)
                .map(PeriodicReportResponse::mapToResponse)
                .orElse(PeriodicReportResponse.emptyResponse());
    }
}
