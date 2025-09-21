package com.project.sleep.domain.domain.entity;

import com.github.f4b6a3.tsid.TsidCreator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "daily_sleep_record")
public class DailySleepRecord {

    @Id
    @Field("daily_sleep_record_no")
    private String dailySleepRecordNo = TsidCreator.getTsid().toString();

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
    private String userNo; // FK → User
}
