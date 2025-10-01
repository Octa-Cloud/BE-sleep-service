package com.project.sleep.domain.application.dto.response;

import com.project.sleep.domain.domain.entity.SleepGoal;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record SleepGoalResponse(
        LocalTime goalBedTime,
        LocalTime goalWakeTime,
        Integer goalTotalSleepTime
) {
    public static SleepGoalResponse from(SleepGoal entity) {
        return SleepGoalResponse.builder()
                .goalBedTime(entity.getGoalBedTime())
                .goalWakeTime(entity.getGoalWakeTime())
                .goalTotalSleepTime(entity.getGoalTotalSleepTime())
                .build();
    }
}
