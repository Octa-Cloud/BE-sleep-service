package com.project.sleep.domain.application.dto.response;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record SleepPatternsResponse(
        LocalDate date,
        int score,
        int totalSleepTime
) { }
