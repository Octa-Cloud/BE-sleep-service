package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.TotalSleepRecordResponse;
import com.project.sleep.domain.application.usecase.GetTotalSleepRecordUseCase;
import com.project.sleep.domain.ui.spec.GetTotalSleepRecordApiSpec;
import com.project.sleep.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetTotalSleepRecordController implements GetTotalSleepRecordApiSpec {

    private final GetTotalSleepRecordUseCase getTotalSleepRecordUseCase;

    @Override
    public BaseResponse<TotalSleepRecordResponse> get(
            Long userNo
    ) {
        return BaseResponse.onSuccess(getTotalSleepRecordUseCase.execute(userNo));
    }
}