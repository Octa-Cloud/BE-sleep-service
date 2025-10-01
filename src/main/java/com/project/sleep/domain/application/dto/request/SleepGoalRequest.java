package com.project.sleep.domain.application.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record SleepGoalRequest(
        @NotNull(message = "목표 취침 시각은 필수입니다.") LocalTime goalBedTime,
        @NotNull(message = "목표 기상 시각은 필수입니다.") LocalTime goalWakeTime,
        @NotNull(message = "목표 총 수면 시간은 필수입니다.") Integer goalTotalSleepTime
) {}
