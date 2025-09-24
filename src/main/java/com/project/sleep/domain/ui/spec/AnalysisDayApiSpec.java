package com.project.sleep.domain.ui.spec;

import com.project.sleep.domain.application.dto.response.AnalysisDayResponse;
import com.project.sleep.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;

@Tag(name = "Sleep Analysis")
public interface AnalysisDayApiSpec {

    @Operation(
            summary = "일간 수면 분석 조회",
            description = "수면 단계별 시간/비율, AI 분석 리포트(analysis), 뇌파•소음 분석, 수면 기록 메모를 반환합니다."

    )
    BaseResponse<AnalysisDayResponse> getDailyAnalysis(
            @Parameter(hidden = true,description = "JWT에서 추출된 사용자 ID") Long userId,
            @Parameter(description = "조회 날짜 (yyyy-MM-dd). 미지정 시 오늘", example = "2025-09-22") LocalDate date
    );
}
