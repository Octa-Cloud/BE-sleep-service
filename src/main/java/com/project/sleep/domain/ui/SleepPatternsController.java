package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.GetSleepPatternsResponse;
import com.project.sleep.domain.application.usecase.GetSleepPatternsUseCase;
import com.project.sleep.domain.ui.spec.SleepPatternsApiSpec;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SleepPatternsController implements SleepPatternsApiSpec {

    private final GetSleepPatternsUseCase getSleepPatternsUseCase;

    @Override
    public BaseResponse<List<GetSleepPatternsResponse>> getSleepPatterns(
            @CurrentUser Long userNo,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        List<GetSleepPatternsResponse> result = getSleepPatternsUseCase.getSleepPatterns(userNo, startDate, endDate);
        return BaseResponse.onSuccess(result);
    }
}
