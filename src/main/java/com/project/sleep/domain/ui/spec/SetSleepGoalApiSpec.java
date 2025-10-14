package com.project.sleep.domain.ui.spec;

import com.project.sleep.domain.application.dto.request.SleepGoalRequest;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Sleep")
public interface SetSleepGoalApiSpec {

    @Operation(
            summary = "목표 수면 시간 설정 API",
            description = "목표 수면 시간을 설정합니다."
    )
    @PostMapping("/api/sleep/goal")
    BaseResponse<Void> set(
            @CurrentUser
            @Parameter(hidden = true)
            Long userNo,
            @RequestBody(
                    description = "목표 수면 시간 설정값",
                    required = true
            )

            SleepGoalRequest request
    );
}
