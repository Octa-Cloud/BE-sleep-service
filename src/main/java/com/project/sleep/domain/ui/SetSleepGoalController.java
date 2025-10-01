package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.request.SleepGoalRequest;
import com.project.sleep.domain.application.usecase.SetSleepGoalUseCase;
import com.project.sleep.domain.ui.spec.SetSleepGoalApiSpec;
import com.project.sleep.global.common.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SetSleepGoalController implements SetSleepGoalApiSpec {

    private final SetSleepGoalUseCase setSleepGoalUseCase;

    @Override
    public BaseResponse<Void> set(
            Long userNo,
            @Valid @RequestBody SleepGoalRequest request
    ) {
        setSleepGoalUseCase.execute(userNo, request);
        return BaseResponse.onSuccess();
    }
}
