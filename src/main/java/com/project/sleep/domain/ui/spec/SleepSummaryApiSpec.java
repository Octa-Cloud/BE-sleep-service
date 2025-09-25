package com.project.sleep.domain.ui.spec;

import com.project.sleep.domain.application.dto.response.SleepSummaryResponse;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "SleepSummary", description = "수면 기록 관련 API")
@RequestMapping("/api/sleep/summary")
public interface SleepSummaryApiSpec {

    @Operation(
            summary = "일별 수면 기록 조회",
            description = "특정 사용자의 특정 날짜에 대한 하루치 수면 기록 요약 정보를 조회합니다. 유저 번호는 토큰에서 추출됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(schema = @Schema(implementation = BaseResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 날짜에 수면 기록이 없을 경우",
                            content = @Content(schema = @Schema(implementation = BaseResponse.class))
                    )
            }
    )
    @GetMapping("/daily")
    ResponseEntity<BaseResponse<List<SleepSummaryResponse>>> getDailySleepSummary(
            @Parameter(hidden = true) @CurrentUser Long userNo,
            @Parameter(description = "조회하려는 날짜 (yyyy-MM-dd)", required = true) @RequestParam LocalDate date
    );

    @Operation(
            summary = "최근 수면 기록 8개 조회",
            description = "특정 사용자의 가장 최근 8개 수면 기록 요약 정보를 조회합니다. 유저 번호는 토큰에서 추출됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(schema = @Schema(implementation = BaseResponse.class))
                    )
            }
    )
    @GetMapping("/recent")
    ResponseEntity<BaseResponse<List<SleepSummaryResponse>>> getRecentSleepSummary(
            @Parameter(hidden = true) @CurrentUser Long userNo
    );
}
