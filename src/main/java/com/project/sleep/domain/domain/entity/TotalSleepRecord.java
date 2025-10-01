package com.project.sleep.domain.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalTime;

@Getter
@Builder
@Document(collection = "total_sleep_record")
@AllArgsConstructor
@NoArgsConstructor
public class TotalSleepRecord {

    @Id
    @Field(name = "total_sleep_record_no")
    private String totalSleepRecordNo;

    @Field(name = "avg_score")
    private Integer avgScore;

    @Field(name = "avg_sleep_time")
    private Integer avgSleepTime;

    @Field(name = "avg_bed_time")
    private LocalTime avgBedTime;

    @Field(name = "user_no")
    private Long userNo;

}
