package com.project.sleep.domain.domain.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@Builder
@Document(collection = "daily_report")
@Getter
public class DailyReport {

    // Mongo의 _id(ObjectId)
    @MongoId(FieldType.OBJECT_ID)      // 또는: @Id private String id;
    private String id;

//    @Id   이 부분 때문에 오류발생 -> 포스트맨 체크시 500을 리턴
    @Field("daily_report_no")
    private Long dailyReportNo;

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
    private List<Double> microwaveGrades= new ArrayList<>();

    @Field("noise_event_types")
    private List<String> noiseEventTypes= new ArrayList<>();

    @Field("analysis_title")
    private String analysisTitle;

    @Field("analysis_description")
    private String analysisDescription;

    @Field("analysis_step")
    private List<String> analysisSteps= new ArrayList<>();

    @Field("analysis_difficulty")
    private String analysisDifficulty;

    @Field("analysis_effect")
    private String analysisEffect;

    @Field("user_no")
    private Long userNo; // FK → User
}

