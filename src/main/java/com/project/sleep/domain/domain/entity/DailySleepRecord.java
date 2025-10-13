package com.project.sleep.domain.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "daily_sleep_record")
@CompoundIndexes({
        @CompoundIndex(name = "userNo_sleepDate_idx", def = "{'user_no': 1, 'sleep_date': -1}")
})
public class DailySleepRecord {

    @Id
    @Field("daily_sleep_record_no")
    private String dailySleepRecordNo;

    @Field("sleep_date")
    private LocalDate sleepDate;

    private int score;

    @Field("bed_time")
    private LocalTime bedTime;

    @Field("wake_time")
    private LocalTime wakeTime;

    @Field("total_sleep_time")
    private int totalSleepTime;

    @Field("user_no")
    private Long userNo;

}
