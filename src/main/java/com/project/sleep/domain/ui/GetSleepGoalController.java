package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.SleepGoalResponse;
import com.project.sleep.domain.application.usecase.GetSleepGoalUseCase;
import com.project.sleep.domain.ui.spec.GetSleepGoalApiSpec;
import com.project.sleep.global.common.BaseResponse;
import com.project.sleep.global.util.ETagGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequiredArgsConstructor
public class GetSleepGoalController implements GetSleepGoalApiSpec {

    private final GetSleepGoalUseCase getSleepGoalUseCase;
    private final ETagGenerator etagGenerator;

    @Override
    public ResponseEntity<BaseResponse<SleepGoalResponse>> get(Long userNo, WebRequest request) {
//        return BaseResponse.onSuccess(getSleepGoalUseCase.execute(userNo));
        SleepGoalResponse sleepGoalData = getSleepGoalUseCase.execute(userNo);
        String etag = etagGenerator.generate(sleepGoalData);

        if (request.checkNotModified(etag)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .eTag(etag)
                    .build();
        }

        return ResponseEntity
                .ok()
                .eTag(etag)
                .body(BaseResponse.onSuccess(sleepGoalData));
    }
}
