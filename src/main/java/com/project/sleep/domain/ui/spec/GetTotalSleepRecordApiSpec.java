package com.project.sleep.domain.ui.spec;

import com.project.sleep.domain.application.dto.response.TotalSleepRecordResponse;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Sleep")
public interface GetTotalSleepRecordApiSpec {

    @Operation(
            summary = "누적 수면 기록 조회 API",
            description = "누적 수면 기록(평균 수면 시간, 평균 수면 점수, 평균 취침 시각)을 조회합니다."
    )
    @PostMapping("/api/sleep/total")
    BaseResponse<TotalSleepRecordResponse> get(
            @CurrentUser
            @Parameter(hidden = true)
            Long userNo
    );
}
