package com.project.sleep.domain.application.dto.response;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.project.sleep.domain.domain.entity.PeriodicReport;
import lombok.Builder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public record PeriodicReportResponse(
        Integer score,
        Integer totalSleepTime,
        LocalTime bedTime,
        Double deepSleepRatio,
        Double lightSleepRatio,
        Double remSleepRatio,
        String improvement,
        String weakness,
        String recommendation,
        String predictDescription,
        List<Integer> scorePrediction
){

    public static PeriodicReportResponse mapToResponse(PeriodicReport report){

        return new PeriodicReportResponse(
                report.getAvgScore(),
                report.getAvgSleepTime(),
                report.getAvgBedTime(),
                report.getDeepSleepRatio(),
                report.getLightSleepRatio(),
                report.getRemSleepRatio(),
                report.getImprovement(),
                report.getWeakness(),
                report.getRecommendation(),
                report.getPredictDescription(),
                report.getScorePrediction()
        );

    }
    public static PeriodicReportResponse emptyResponse(){
        return new PeriodicReportResponse(
                0,
                0,
                null,
                0.0,
                0.0,
                0.0,
                "",
                "",
                "",
                "",
                List.of()
        );
    }
}
