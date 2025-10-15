package com.project.sleep.domain.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Builder
@Document(collection = "daily_report")
@CompoundIndex(
        name = "uniq_user_sleepDate",
        def = "{ 'user_no': 1, 'sleep_date': 1 }"
)// date가 null인 문서들이 있어 unique 적용 생략
@AllArgsConstructor
public class DailyReport {

    @Id
    @Field(value = "daily_report_no")
    private String dailyReportNo;

    @Field(value = "sleep_date")
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

    @Field("analysis_title")
    private String analysisTitle;

    @Field("analysis_description")
    private String analysisDescription;

    @Field("analysis_step")
    private List<String> analysisSteps;

    @Field("analysis_difficulty")
    private String analysisDifficulty;

    @Field("analysis_effect")
    private String analysisEffect;

    @Field("user_no")
    private Long userNo; // FK → User
}

