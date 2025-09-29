package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.AnalysisDayResponse;
import com.project.sleep.domain.application.usecase.GetDailyAnalysisUseCase;
import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.ui.spec.GetDailyAnalysisApiSpec;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class GetDailyAnalysisController implements GetDailyAnalysisApiSpec {

    private final GetDailyAnalysisUseCase getDailyAnalysisUseCase;

    @Override
    public BaseResponse<AnalysisDayResponse> getDailyAnalysis(
            Long userNo,
            LocalDate date
    ) {
        DailyReport report = getDailyAnalysisUseCase.getDailyAnalysis(userNo, date);
        return BaseResponse.onSuccess(AnalysisDayResponse.from(report));
    }
}
