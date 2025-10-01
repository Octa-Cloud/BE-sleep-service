package com.project.sleep.domain.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Builder
@Document(collection = "total_sleep_record")
@AllArgsConstructor
@NoArgsConstructor
public class TotalSleepRecord {

    @Id
    @Field(name = "total_sleep_record_no")
    private String totalSleepRecordNo;


}
