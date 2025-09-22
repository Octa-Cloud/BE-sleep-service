package com.project.sleep.domain.application.dto.response;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;
import java.time.LocalDate;

@Getter
@Setter
public class SleepSummaryResponse {

    private int score;
    private int totalSleepTime;
    private LocalTime bedTime;
    private LocalTime wakeTime;
    private LocalDate date;

    public static SleepSummaryResponse from(DailySleepRecord record) {
        SleepSummaryResponse response = new SleepSummaryResponse();
        response.setScore(record.getScore());
        response.setTotalSleepTime(record.getTotalSleepTime());
        response.setBedTime(record.getBedTime());
        response.setWakeTime(record.getWakeTime());
        response.setDate(record.getSleepDate());
        return response;
    }
}