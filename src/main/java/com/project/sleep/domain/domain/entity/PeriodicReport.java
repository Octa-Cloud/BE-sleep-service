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
    private String bedTime;

    @Field("deep_sleep_ratio")
    private double deepSleepRatio;

    @Field("light_sleep_ratio")
    private double lightSleepRatio;

    @Field("rem_sleep_ratio")
    private double remSleepRatio;

    private String improvement;
    private String weakness;
    private String recommendation;

    // 임베디드 컬럼이었지만 굳이 필요 없어서 예측 점수만 가진 리스트로 변경
    @Field("score_prediction")
    private Prediction scorePrediction;

    @Field("user_no")
    private String userNo; // FK → User

    public enum Type {
        weekly, monthly
    }
}
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class Prediction{
    private String description;
    private List<Integer> scorePrediction;
}