package com.project.sleep.domain.ui.spec;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.global.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "Sleep")
public interface MonthlyReportApiSpec {

    @Operation(
            summary = "월간 통계 조회 API",
            description = "월간 시작 날짜를 선택 후, 평균수치와 AI 점수를 반환합니다."
    )
    @GetMapping("/api/sleep/analysis/monthly")
    PeriodicReportResponse getWeeklyReport(
            @Parameter(hidden = true) Long userNo,
            @Parameter(description = "조회 기준 날짜 (해당 월의 시작 날짜를 반환)") LocalDate date
    );
}
