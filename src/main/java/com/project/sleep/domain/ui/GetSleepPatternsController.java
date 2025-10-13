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
    public ResponseEntity<BaseResponse<List<SleepPatternsResponse>>> getSleepPatterns(
            Long userNo,
            LocalDate startDate,
            LocalDate endDate,
            WebRequest request
    ) {
//        return BaseResponse.onSuccess(getSleepPatternsUseCase.getSleepPatterns(userNo, startDate, endDate));
        // 실제 데이터 조회 (UseCase 호출)
        List<SleepPatternsResponse> sleepPatternsData = getSleepPatternsUseCase.getSleepPatterns(userNo, startDate, endDate);

        // 조회된 데이터로 ETag 생성
        String etag = etagGenerator.generate(sleepPatternsData);

        // ETag 비교
        if (request.checkNotModified(etag)) {
            // ETag가 동일하면 304 응답 반환
            return ResponseEntity.status(304).eTag(etag).build();
        }

        // ETag가 다르면 200 OK와 함께 데이터 반환
        return ResponseEntity
                .ok()
                .eTag(etag)
                .body(BaseResponse.onSuccess(sleepPatternsData));
    }
}
