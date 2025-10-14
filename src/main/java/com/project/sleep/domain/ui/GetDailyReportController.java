package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.DailyReportResponse;
import com.project.sleep.domain.application.usecase.GetDailyReportUseCase;
import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.ui.spec.GetDailyReportApiSpec;
import com.project.sleep.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class GetDailyReportController implements GetDailyReportApiSpec {

    private final GetDailyReportUseCase getDailyReportUseCase;

    @Override
    public BaseResponse<DailyReportResponse> getDailyAnalysis(
            Long userNo,
            LocalDate date
    ) {
        DailyReport report = getDailyReportUseCase.execute(userNo, date);
        return BaseResponse.onSuccess(DailyReportResponse.from(report));
    }
}
