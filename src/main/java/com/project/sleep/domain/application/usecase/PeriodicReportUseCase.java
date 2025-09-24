package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.domain.domain.service.PeriodicReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class PeriodicReportUseCase {
    private final PeriodicReportService periodicReportService;

    public PeriodicReportResponse getPeriodicReport(String type, Long userNo, LocalDate date){
        return periodicReportService.getReport(type, userNo, date)
                .map(PeriodicReportResponse::mapToResponse)
                .orElse(PeriodicReportResponse.emptyResponse());

    }
}
