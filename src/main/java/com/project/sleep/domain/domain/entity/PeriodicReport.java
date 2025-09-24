package com.project.sleep.domain.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "periodic_report")
public class PeriodicReport {

    @Id
    @Field("periodic_report_no")
    private String periodicReportNo;

    private Type type; // Enum('weekly', 'monthly')

    private LocalDate date;

    private int score;

    @Field("total_sleep_time")
    private int totalSleepTime;

    @Field("bed_time")
    private LocalDateTime bedTime;

    @Field("deep_sleep_ratio")
    private double deepSleepRatio;

    @Field("light_sleep_ratio")
    private double lightSleepRatio;

    @Field("rem_sleep_ratio")
    private double remSleepRatio;

    private String improvement;
    private String weakness;
    private String recommendation;

    @Field("predict_description")
    private String predictDescription;

    @Field("score_prediction")
    private List<Integer> scorePrediction = new ArrayList();

    @Field("user_no")
    private String userNo; // FK → User

    public enum Type{
        weekly, monthly
    }

}
