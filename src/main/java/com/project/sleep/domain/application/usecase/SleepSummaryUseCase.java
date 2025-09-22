package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.SleepSummaryResponse;
import java.time.LocalDate;
import java.util.List;

public interface SleepSummaryUseCase {
    List<SleepSummaryResponse> getDailySummary(LocalDate date);
    SleepSummaryResponse getRecentSummary(LocalDate date);
}