package com.project.sleep.domain.ui.spec;

import com.project.sleep.domain.application.dto.response.SleepGoalResponse;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Tag(name = "Sleep")
public interface GetSleepGoalApiSpec {

    @Operation(
            summary = "목표 수면 시간 조회 API",
            description = "목표 수면 시간을 조회합니다."
    )
    @GetMapping("/api/sleep/goal")
    ResponseEntity<BaseResponse<SleepGoalResponse>> get(
            @Parameter(hidden = true)
            @CurrentUser
            Long userNo,

            WebRequest request
    );
}
