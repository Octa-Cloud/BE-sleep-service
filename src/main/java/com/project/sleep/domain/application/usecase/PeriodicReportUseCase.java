package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.domain.domain.service.PeriodicReportService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PeriodicReportUseCase {
    private final PeriodicReportService periodicReportService;

    public PeriodicReportUseCase(PeriodicReportService periodicReportService) {
        this.periodicReportService = periodicReportService;
    }

    public PeriodicReportResponse getPeriodicAnalysis(String type, Long userNo, LocalDate date){
        return periodicReportService.getAnalysis(type, userNo, date);
    }
}
