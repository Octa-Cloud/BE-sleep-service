package com.project.sleep.domain.application.dto.response;

import com.project.sleep.domain.domain.entity.DailySleepRecord;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

// record로 변경
public record SleepSummaryResponse(
        int score,
        int totalSleepTime,
        LocalTime bedTime,
        LocalTime wakeTime,
        LocalDate date
) implements Serializable { // Serializable 인터페이스 구현

    public static SleepSummaryResponse from(DailySleepRecord record) {
        return new SleepSummaryResponse(
                record.getScore(),
                record.getTotalSleepTime(),
                record.getBedTime(),
                record.getWakeTime(),
                record.getSleepDate()
        );
    }
}