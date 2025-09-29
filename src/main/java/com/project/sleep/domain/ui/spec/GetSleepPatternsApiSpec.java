package com.project.sleep.domain.ui.spec;

import com.project.sleep.domain.application.dto.response.GetSleepPatternsResponse;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Sleep")
public interface GetSleepPatternsApiSpec {

    @Operation(
            summary = "특정 기간 수면 시간 및 점수 조회 API",
            description = "사용자의 특정 기간(startDate, endDate)에 해당하는 일별 수면 데이터(날짜, 점수, 총 수면 시간) 리스트를 조회합니다."
    )
    @GetMapping("/api/sleep/patterns")
    BaseResponse<List<GetSleepPatternsResponse>> getSleepPatterns(
            @Parameter(hidden = true) // Swagger UI에서는 숨김 처리
            @CurrentUser Long userNo,

            @Parameter(name = "startDate", description = "조회 시작일 (YYYY-MM-DD)", required = true, example = "2025-09-01")
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,

            @Parameter(name = "endDate", description = "조회 종료일 (YYYY-MM-DD)", required = true, example = "2025-09-30")
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    );
}