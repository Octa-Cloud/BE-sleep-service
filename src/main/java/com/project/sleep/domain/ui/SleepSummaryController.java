package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.SleepSummaryResponse;
import com.project.sleep.domain.application.usecase.SleepSummaryUseCase;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
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
public class SleepSummaryController {

    private final SleepSummaryUseCase sleepSummaryUseCase;

    // Daily API 로직
    @GetMapping("/daily")
    public ResponseEntity<BaseResponse<List<SleepSummaryResponse>>> getDailySleepSummary(@CurrentUser Long userNo, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<SleepSummaryResponse> dailySummaries = sleepSummaryUseCase.getDailySummary(userNo, date);

        return ResponseEntity.ok(BaseResponse.onSuccess(dailySummaries));
    }

    // Recent API 로직
    @GetMapping("/recent")
    public ResponseEntity<BaseResponse<List<SleepSummaryResponse>>> getRecentSleepSummary(@CurrentUser Long userNo) {
        List<SleepSummaryResponse> recentSummaries = sleepSummaryUseCase.getRecentSummary(userNo);

        return ResponseEntity.ok(BaseResponse.onSuccess(recentSummaries));
    }
}
