package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.domain.domain.entity.PeriodicReport;
import com.project.sleep.domain.domain.service.PeriodicReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class PeriodicReportUseCase {
    private final PeriodicReportService periodicReportService;

    public PeriodicReportResponse getPeriodicReport(PeriodicReport.Type type, Long userNo, LocalDate date){
        return periodicReportService.getReport(type, userNo, date)
                .map(PeriodicReportResponse::mapToResponse)
                .orElse(PeriodicReportResponse.emptyResponse());

    }
}
