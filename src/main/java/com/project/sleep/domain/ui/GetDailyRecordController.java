package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.AnalysisDayResponse;
import com.project.sleep.domain.application.usecase.GetDailyRecordUseCase;
import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.ui.spec.GetDailyRecordApiSpec;
import com.project.sleep.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class GetDailyRecordController implements GetDailyRecordApiSpec {

    private final GetDailyRecordUseCase getDailyRecordUseCase;

    @Override
    public BaseResponse<AnalysisDayResponse> getDailyAnalysis(
            Long userNo,
            LocalDate date
    ) {
        DailyReport report = getDailyRecordUseCase.execute(userNo, date);
        return BaseResponse.onSuccess(AnalysisDayResponse.from(report));
    }
}
