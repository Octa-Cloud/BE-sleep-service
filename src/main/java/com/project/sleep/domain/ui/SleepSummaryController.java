package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.SleepSummaryResponse;
import com.project.sleep.domain.application.usecase.SleepSummaryUseCase;
import com.project.sleep.domain.ui.spec.SleepSummaryApiSpec;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SleepSummaryController implements SleepSummaryApiSpec {

    private final SleepSummaryUseCase sleepSummaryUseCase;

    @Override
    public BaseResponse<SleepSummaryResponse> getDailySleepSummary(@CurrentUser Long userNo, LocalDate date) {
        SleepSummaryResponse dailySummary = sleepSummaryUseCase.getDailySummary(userNo, date);
        return BaseResponse.onSuccess(dailySummary);
    }

    @Override
    public BaseResponse<List<SleepSummaryResponse>> getRecentSleepSummary(@CurrentUser Long userNo) {
        List<SleepSummaryResponse> recentSummaries = sleepSummaryUseCase.getRecentSummary(userNo);
        return BaseResponse.onSuccess(recentSummaries);
    }
}
