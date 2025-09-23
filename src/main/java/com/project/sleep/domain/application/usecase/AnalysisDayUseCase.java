package com.project.sleep.domain.application.usecase;


import com.project.sleep.domain.application.dto.response.AnalysisDayResponse;
import java.time.LocalDate;

public interface  AnalysisDayUseCase {
    AnalysisDayResponse getDailyAnalysis( Long userId, LocalDate date);
}