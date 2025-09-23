package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.GetSleepPatternsResponse;
import com.project.sleep.domain.application.usecase.GetSleepPatternsUseCase;
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
@RequiredArgsConstructor
@RequestMapping("/api/sleep/patterns")
public class SleepPatternsController {

    private final GetSleepPatternsUseCase getSleepPatternsUseCase;

    @GetMapping
    public ResponseEntity<List<GetSleepPatternsResponse>> getSleepPatterns(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        List<GetSleepPatternsResponse> result = getSleepPatternsUseCase.getSleepPatterns(startDate, endDate);

        return ResponseEntity.ok(result);
    }
}