package com.project.sleep.domain.domain.entity;

import com.github.f4b6a3.tsid.TsidCreator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "daily_report")
public class DailyReport {

    @Id
    @Field("daily_report_no")
    private String dailyReportNo;

    @Field("sleep_date")
    private LocalDate sleepDate;

    @Field("deep_sleep_time")
    private int deepSleepTime;

    @Field("light_sleep_time")
    private int lightSleepTime;

    @Field("rem_sleep_time")
    private int remSleepTime;

    @Field("deep_sleep_ratio")
    private double deepSleepRatio;

    @Field("light_sleep_ratio")
    private double lightSleepRatio;

    @Field("rem_sleep_ratio")
    private double remSleepRatio;

    private String memo;

    @Field("microwave_grades")
    private List<Double> microwaveGrades;

    @Field("noise_event_types")
    private List<String> noiseEventTypes;

    private List<Analysis> analysis;

    @Field("user_no")
    private String userNo; // FK → User

}
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class Analysis{
    private String title;
    private String description;
    private List<String> steps;
    private String difficulty;
    private String effect;
}

