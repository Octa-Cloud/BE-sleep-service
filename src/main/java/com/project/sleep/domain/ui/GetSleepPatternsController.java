package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.SleepPatternsResponse;
import com.project.sleep.domain.application.usecase.GetSleepPatternsUseCase;
import com.project.sleep.domain.ui.spec.GetSleepPatternsApiSpec;
import com.project.sleep.global.common.BaseResponse;
import com.project.sleep.global.util.ETagGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetSleepPatternsController implements GetSleepPatternsApiSpec {

    private final GetSleepPatternsUseCase getSleepPatternsUseCase;
    private final ETagGenerator etagGenerator;

    @Override
    public ResponseEntity<List<SleepPatternsResponse>> getSleepPatterns(
            Long userNo,
            LocalDate startDate,
            LocalDate endDate,
            WebRequest request
    ) {
        List<SleepPatternsResponse> sleepPatternsData = getSleepPatternsUseCase.getSleepPatterns(userNo, startDate, endDate);
        String etag = etagGenerator.generate(sleepPatternsData);

        if (request.checkNotModified(etag)) {
            return ResponseEntity
                    .status(304)
                    .eTag(etag)
                    .build();
        }

        // ETag가 다르면 200 OK와 함께 데이터 반환
        return ResponseEntity
                .ok()
                .eTag(etag)
                .body(sleepPatternsData);
    }
}
