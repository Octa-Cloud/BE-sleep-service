package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.SleepPatternsResponse;
import com.project.sleep.domain.application.usecase.GetSleepPatternsUseCase;
import com.project.sleep.domain.ui.spec.GetSleepPatternsApiSpec;
import com.project.sleep.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetSleepPatternsController implements GetSleepPatternsApiSpec {

    private final GetSleepPatternsUseCase getSleepPatternsUseCase;

    @Override
    public BaseResponse<List<SleepPatternsResponse>> getSleepPatterns(
            Long userNo,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return BaseResponse.onSuccess(getSleepPatternsUseCase.getSleepPatterns(userNo, startDate, endDate));
    }
}
