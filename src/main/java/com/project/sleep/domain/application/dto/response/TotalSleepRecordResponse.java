package com.project.sleep.domain.application.dto.response;

import com.project.sleep.domain.domain.entity.TotalSleepRecord;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record TotalSleepRecordResponse(
        Integer avgScore,
        Integer avgSleepTime,
        LocalTime avgBedTime
) {
    public static TotalSleepRecordResponse from(TotalSleepRecord entity) {
        return TotalSleepRecordResponse.builder()
                .avgScore(entity.getAvgScore())
                .avgBedTime(entity.getAvgBedTime())
                .avgSleepTime(entity.getAvgSleepTime())
                .build();
    }
}
