package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.TotalSleepRecordResponse;
import com.project.sleep.domain.application.usecase.GetTotalSleepRecordUseCase;
import com.project.sleep.domain.ui.spec.GetTotalSleepRecordApiSpec;
import com.project.sleep.global.util.ETagGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequiredArgsConstructor
public class GetTotalSleepRecordController implements GetTotalSleepRecordApiSpec {

    private final GetTotalSleepRecordUseCase getTotalSleepRecordUseCase;
    private final ETagGenerator eTagGenerator;

    @Override
    public ResponseEntity<TotalSleepRecordResponse> get(
            Long userNo,
            WebRequest request
    ) {
        TotalSleepRecordResponse response = getTotalSleepRecordUseCase.execute(userNo);
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
                .body(response);
    }
}