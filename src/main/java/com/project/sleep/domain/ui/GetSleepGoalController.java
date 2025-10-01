package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.SleepGoalResponse;
import com.project.sleep.domain.application.usecase.GetSleepGoalUseCase;
import com.project.sleep.domain.ui.spec.GetSleepGoalApiSpec;
import com.project.sleep.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetSleepGoalController implements GetSleepGoalApiSpec {

    private final GetSleepGoalUseCase getSleepGoalUseCase;

    @Override
    public BaseResponse<SleepGoalResponse> get(Long userNo) {
        return BaseResponse.onSuccess(getSleepGoalUseCase.execute(userNo));
    }
}
