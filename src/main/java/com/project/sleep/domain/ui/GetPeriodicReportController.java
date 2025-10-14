package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.domain.application.usecase.GetPeriodicReportUseCase;
import com.project.sleep.domain.ui.spec.GetPeriodicReportApiSpec;
import com.project.sleep.global.util.ETagGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class GetPeriodicReportController implements GetPeriodicReportApiSpec {

    private final GetPeriodicReportUseCase getPeriodicReportUseCase;
    private final ETagGenerator eTagGenerator;

    @Override
    public ResponseEntity<PeriodicReportResponse> getWeeklyReport(
            Long userNo,
            LocalDate date,
            WebRequest request
    ) {
        PeriodicReportResponse response = getPeriodicReportUseCase.getWeeklyReport(userNo, date);
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


    @Override
    public ResponseEntity<PeriodicReportResponse> getMonthlyReport(
            Long userNo,
            LocalDate date,
            WebRequest request
    ) {
        PeriodicReportResponse response = getPeriodicReportUseCase.getMonthlyReport(userNo, date);
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