package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.DailyReportResponse;
import com.project.sleep.domain.application.usecase.GetDailyReportUseCase;
import com.project.sleep.domain.ui.spec.GetDailyReportApiSpec;
import com.project.sleep.global.util.ETagGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class GetDailyReportController implements GetDailyReportApiSpec {

    private final GetDailyReportUseCase getDailyReportUseCase;
    private final ETagGenerator eTagGenerator;

    @Override
    public ResponseEntity<DailyReportResponse> getDailyAnalysis(
            Long userNo,
            LocalDate date,
            WebRequest request
    ) {
        DailyReportResponse response = getDailyReportUseCase.execute(userNo, date);
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
