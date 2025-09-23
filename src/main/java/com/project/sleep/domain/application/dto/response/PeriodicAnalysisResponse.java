package com.project.sleep.domain.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PeriodicAnalysisResponse
{
    private int avgScore;
    private int avgSleepTime;
    private LocalTime avgBedTime;
    private double avgDeepRatio;
    private double avgLightRatio;
    private double avgRemRatio;
    private String improvement;
    private String weakness;
    private String recommendation;
    private String predictDescription;
    private List<Integer> scorePrediction;
}
