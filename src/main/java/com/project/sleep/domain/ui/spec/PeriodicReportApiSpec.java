package com.project.sleep.domain.ui.spec;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Tag(name = "Sleep")
@RequestMapping("/api/sleep/analysis")
public interface PeriodicReportApiSpec {

    @Operation(
            summary = "주간 통계 조회 API",
            description = "주간 시작 날짜를 선택 후,"
                    + "평균수치와 AI 점수를 반환합니다."
    )
    @GetMapping("/weekly")
    BaseResponse<PeriodicReportResponse> getWeeklyReport(
            @Parameter(hidden = true) Long userNo,
            @Parameter(description = "조회 기준 날짜 (해당 주의 시작 날짜를 반환)") LocalDate date
    );

    @Operation(
            summary = "월간 통계 조회 API",
            description = "월간 시작 날짜를 선택 후,"
                    + "평균수치와 AI 점수를 반환합니다."
    )
    @GetMapping("/monthly")
    BaseResponse<PeriodicReportResponse> getMonthlyReport(
            @Parameter(hidden = true) Long userNo,
            @Parameter(description = "조회 기준 날짜 (해당 월의 시작 날짜를 반환)") LocalDate date
    );
}
