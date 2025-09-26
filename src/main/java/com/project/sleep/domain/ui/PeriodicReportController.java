package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.domain.application.usecase.PeriodicReportUseCase;
import com.project.sleep.domain.ui.spec.PeriodicReportApiSpec;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static com.project.sleep.domain.domain.entity.PeriodicReport.Type.MONTHLY;
import static com.project.sleep.domain.domain.entity.PeriodicReport.Type.WEEKLY;

@RequiredArgsConstructor
@RestController
public class PeriodicReportController implements PeriodicReportApiSpec {

    private final PeriodicReportUseCase periodicReportUseCase;


    @Override
    public BaseResponse<PeriodicReportResponse> getWeeklyReport(
            @CurrentUser Long userNo,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return BaseResponse.onSuccess(periodicReportUseCase.getPeriodicReport(WEEKLY, userNo, date));
    }


    @Override
    public BaseResponse<PeriodicReportResponse> getMonthlyReport(
            @CurrentUser Long userNo,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return BaseResponse.onSuccess(periodicReportUseCase.getPeriodicReport(MONTHLY, userNo, date));

    }
}