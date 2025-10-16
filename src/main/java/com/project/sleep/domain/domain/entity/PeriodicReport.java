package com.project.sleep.domain.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "periodic_report")
@CompoundIndexes({
        @CompoundIndex(name = "user_type_date_idx", def = "{'user_no': 1, 'type': 1, 'start_date': -1}")
})
public class PeriodicReport {

    @Id
    @Field("periodic_report_no")
    private String periodicReportNo;

    private Type type; // Enum('weekly', 'monthly')

    @Field("start_date")
    private LocalDate startDate;

    @Field("end_date")
    private LocalDate endDate;

    @Field("avg_score")
    private int avgScore;

    @Field("avg_sleep_time")
    private int avgSleepTime;

    @Field("avg_bed_time")
    private LocalTime avgBedTime;

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
    private Long userNo; // FK → User

    public enum Type{
        WEEKLY, MONTHLY
    }

}
