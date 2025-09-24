package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.domain.application.usecase.PeriodicReportUseCase;
import com.project.sleep.global.annotation.CurrentUser;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/sleep/analysis")
public class PeriodicReportController {

    private final PeriodicReportUseCase periodicReportUseCase;

    public PeriodicReportController(PeriodicReportUseCase periodicReportUseCase) {
        this.periodicReportUseCase = periodicReportUseCase;
    }

    @GetMapping("/weekly")
    public PeriodicReportResponse getWeeklyAnalysis(
            @CurrentUser Long userNo,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return periodicReportUseCase.getPeriodicAnalysis("weekly", userNo, date);
    }
    @GetMapping("/monthly")
    public PeriodicReportResponse getMonthlyAnalysis(
            @CurrentUser Long userNo,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return periodicReportUseCase.getPeriodicAnalysis("monthly", userNo, date);

    }
}
