package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.SleepSummaryResponse;
import com.project.sleep.domain.application.usecase.SleepSummaryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sleep/summary")
@RequiredArgsConstructor
public class SleepController {

    private final SleepSummaryUseCase sleepSummaryUseCase;

    @GetMapping("/daily")
    public ResponseEntity<List<SleepSummaryResponse>> getDailySleepSummary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<SleepSummaryResponse> dailySummaries = sleepSummaryUseCase.getDailySummary(date);
        return ResponseEntity.ok(dailySummaries);
    }

    @GetMapping("/recent")
    public ResponseEntity<SleepSummaryResponse> getRecentSleepSummary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        SleepSummaryResponse recentSummary = sleepSummaryUseCase.getRecentSummary(date);
        if (recentSummary == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recentSummary);
    }
}