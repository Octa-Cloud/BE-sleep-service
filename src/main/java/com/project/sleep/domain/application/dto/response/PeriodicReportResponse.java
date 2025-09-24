package com.project.sleep.domain.application.dto.response;


import com.project.sleep.domain.domain.entity.PeriodicReport;

import java.time.LocalDateTime;
import java.util.List;


public record PeriodicReportResponse(
        Integer score,
        Integer totalSleepTime,
        LocalDateTime bedTime,
        Double deepSleepRatio,
        Double lightSleepRatio,
        Double remSleepRatio,
        String improvement,
        String weakness,
        String recommendation,
        String predictDescription,
        List<Integer> scorePrediction


){
    public enum Type{
        weekly, monthly
    }

    public record Prediction(
            String description,
            List<Integer> scorePrediction
    ){}

    public static PeriodicReportResponse mapToResponse(PeriodicReport report){

        return new PeriodicReportResponse(
                report.getScore(),
                report.getTotalSleepTime(),
                report.getBedTime(),
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
                null
        );
    }
}

