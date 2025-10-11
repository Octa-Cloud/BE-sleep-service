package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.SleepSummaryResponse;
import com.project.sleep.domain.application.usecase.GetSleepSummaryUseCase;
import com.project.sleep.domain.ui.spec.GetSleepSummaryApiSpec;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
import com.project.sleep.global.util.ETagGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GetSleepSummaryController implements GetSleepSummaryApiSpec {

    private final GetSleepSummaryUseCase getSleepSummaryUseCase;
    private final ETagGenerator eTagGenerator;

    @Override
    public ResponseEntity<BaseResponse<SleepSummaryResponse>> getDailySleepSummary(  // 반환 타입 변경
         Long userNo,
         LocalDate date,
         WebRequest request  // 파라미터 추가
    ) {
        SleepSummaryResponse response = getSleepSummaryUseCase.getDailySummary(userNo, date);
        String etag = eTagGenerator.generate(response);

        if (request.checkNotModified(etag)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .eTag(etag)
                    .build();
        }

        return ResponseEntity
                .ok()
                .eTag(etag)
                .body(BaseResponse.onSuccess(response));
    }
    @Override
    public BaseResponse<List<SleepSummaryResponse>> getRecentSleepSummary(
            Long userNo
    ) {
        return BaseResponse.onSuccess(getSleepSummaryUseCase.getRecentSummary(userNo));
    }
}
