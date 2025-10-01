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
@Document(collection = "sleep_goal")
@AllArgsConstructor
@NoArgsConstructor
public class SleepGoal {

    @Id
    @Field(name = "goal_no")
    private String goalNo;

    @Field(name = "user_no")
    private Long userNo;

    @Field(name = "goal_bed_time")
    private LocalTime goalBedTime;

    @Field(name = "goal_wake_time")
    private LocalTime goalWakeTime;

    @Field(name = "goal_total_sleep_time")
    private Integer goalTotalSleepTime;

    public void update(LocalTime goalBedTime, LocalTime goalWakeTime, Integer goalTotalSleepTime) {
        this.goalBedTime = goalBedTime;
        this.goalWakeTime = goalWakeTime;
        this.goalTotalSleepTime = goalTotalSleepTime;
    }
}
