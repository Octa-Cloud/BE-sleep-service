package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.request.PeriodicAnalysisRequest;
import com.project.sleep.domain.application.dto.response.PeriodicAnalysisResponse;
import com.project.sleep.domain.domain.service.PeriodicAnalysisService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/sleep/analysis")
public class PeriodicAnalysisController {

    private final PeriodicAnalysisService periodicAnalysisService;

    public PeriodicAnalysisController(PeriodicAnalysisService periodicAnalysisService) {
        this.periodicAnalysisService = periodicAnalysisService;
    }

    @GetMapping("/weekly")
    public PeriodicAnalysisResponse getWeeklyAnalysis(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @RequestParam String userNo) {
        PeriodicAnalysisRequest request = new PeriodicAnalysisRequest();
        request.setType("weekly");
        request.setStartDate(startDate);
        return periodicAnalysisService.getAnalysis(request, userNo);
    }
    @GetMapping("/monthly")
    public PeriodicAnalysisResponse getMonthlyAnalysis(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @RequestParam String userNo) {
        PeriodicAnalysisRequest request = new PeriodicAnalysisRequest();
        request.setType("monthly");
        request.setStartDate(startDate); // yyyy-MM-dd 형식
        return periodicAnalysisService.getAnalysis(request, userNo);
    }
}
