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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "user")
public class User {

    @Id
    @Field("user_no")
    private String userNo = TsidCreator.getTsid().toString();

    private String email;
    private String password;
    private String name;
    private LocalDate birth;
    private Gender gender; // Enum('male', 'female')

    @Field("goal_sleep_time")
    private int goalSleepTime; // 분 단위 (0~1440)

    @Field("goal_bed_time")
    private LocalTime goalBedTime;

    @Field("goal_wake_time")
    private LocalTime goalWakeTime;

    @Field("avg_score")
    private int avgScore;

    @Field("avg_sleep_time")
    private int avgSleepTime;

    @Field("avg_bed_time")
    private LocalTime avgBedTime;

    public enum Gender {
        male, female
    }

}
