package com.project.sleep.domain.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@Builder
@Document(collection = "daily_report")
@Getter
public class DailyReport {

    @Id
    @Field(value = "daily_report_no")
    private String dailyReportNo;

    @Field(value = "sleep_date")
    private LocalDateTime sleepDate;

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
    private List<Double> microwaveGrades = new ArrayList<>();

    @Field("noise_event_types")
    private List<String> noiseEventTypes = new ArrayList<>();

    @Field("analysis_title")
    private String analysisTitle;

    @Field("analysis_description")
    private String analysisDescription;

    @Field("analysis_step")
    private List<String> analysisSteps = new ArrayList<>();

    @Field("analysis_difficulty")
    private String analysisDifficulty;

    @Field("analysis_effect")
    private String analysisEffect;

    @Field("user_no")
    private Long userNo; // FK → User
}

