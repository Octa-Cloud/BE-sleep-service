package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.SleepSummaryResponse;
import com.project.sleep.domain.application.usecase.GetSleepSummaryUseCase;
import com.project.sleep.domain.ui.spec.GetSleepSummaryApiSpec;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetSleepSummaryController implements GetSleepSummaryApiSpec {

    private final GetSleepSummaryUseCase getSleepSummaryUseCase;

    @Override
    public BaseResponse<SleepSummaryResponse> getDailySleepSummary(
            Long userNo,
            LocalDate date
    ) {
        return BaseResponse.onSuccess(getSleepSummaryUseCase.getDailySummary(userNo, date));
    }

    @Override
    public BaseResponse<List<SleepSummaryResponse>> getRecentSleepSummary(
            Long userNo
    ) {
        return BaseResponse.onSuccess(getSleepSummaryUseCase.getRecentSummary(userNo));
    }
}
