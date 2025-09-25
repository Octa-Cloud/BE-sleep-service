package com.project.sleep.domain.ui;

import com.project.sleep.domain.application.dto.response.AnalysisDayResponse;
import com.project.sleep.domain.application.usecase.AnalysisDayUseCase;
import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.project.sleep.domain.ui.spec.AnalysisDayApiSpec;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/sleep/analysis")
@RequiredArgsConstructor
public class AnalysisDayController implements AnalysisDayApiSpec {

    private final AnalysisDayUseCase analysisDayUseCase;

    @Override
    @GetMapping("/daily")
    public BaseResponse<AnalysisDayResponse> getDailyAnalysis(
            @CurrentUser Long userNo,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        //내부적으로 userNo 넘겨줌, 조회시 유저구분을 위해.
        DailyReport report = analysisDayUseCase.getDailyAnalysis(userNo, date);
        return BaseResponse.onSuccess(AnalysisDayResponse.from(report));
    }
}
