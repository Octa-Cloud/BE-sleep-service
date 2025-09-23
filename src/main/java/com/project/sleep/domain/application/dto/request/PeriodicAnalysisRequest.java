package com.project.sleep.domain.application.dto.request;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Data
@Getter
public class PeriodicAnalysisRequest {
    private String type;
    private LocalDate startDate;
}
