package com.project.sleep.domain.application.dto.response;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record GetSleepPatternsResponse(
        LocalDate date,
        int score,
        int totalSleepTime
) { }
