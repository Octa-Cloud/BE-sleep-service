package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.domain.application.usecase.GetPeriodicReportUseCase;
import com.project.sleep.domain.ui.spec.GetPeriodicReportApiSpec;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static com.project.sleep.domain.domain.entity.PeriodicReport.Type.MONTHLY;
import static com.project.sleep.domain.domain.entity.PeriodicReport.Type.WEEKLY;

@RestController
@RequiredArgsConstructor
public class GetPeriodicReportController implements GetPeriodicReportApiSpec {

    private final GetPeriodicReportUseCase getPeriodicReportUseCase;

    @Override
    public BaseResponse<PeriodicReportResponse> getWeeklyReport(
            Long userNo,
            LocalDate date
    ) {
        return BaseResponse.onSuccess(getPeriodicReportUseCase.getWeeklyReport(WEEKLY, userNo, date));
    }


    @Override
    public BaseResponse<PeriodicReportResponse> getMonthlyReport(
            Long userNo,
            LocalDate date
    ) {
        return BaseResponse.onSuccess(getPeriodicReportUseCase.getMonthlyReport(MONTHLY, userNo, date));
    }
}