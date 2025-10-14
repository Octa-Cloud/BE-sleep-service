package com.project.sleep.domain.ui.spec;

import com.project.sleep.domain.application.dto.response.DailyReportResponse;
import com.project.sleep.global.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

@Tag(name = "Sleep")
public interface GetDailyReportApiSpec {

    @Operation(
            summary = "일간 수면 분석 조회",
            description = "수면 단계별 시간/비율, AI 분석 리포트(analysis), 뇌파•소음 분석, 수면 기록 메모를 반환합니다."

    )
    @GetMapping("/api/sleep/report/daily")
    ResponseEntity<DailyReportResponse> getDailyAnalysis(
            @CurrentUser
            @Parameter(hidden = true,description = "JWT에서 추출된 사용자 ID")
            Long userNo,
            @Parameter(description = "조회 날짜 (yyyy-MM-dd). 미지정 시 오늘", example = "2025-09-22")
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @Parameter(hidden = true)
            WebRequest request
    );
}
