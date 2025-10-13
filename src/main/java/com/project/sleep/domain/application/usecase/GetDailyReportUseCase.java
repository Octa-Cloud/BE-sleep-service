package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.domain.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class GetDailyReportUseCase {

    private final DailyReportService dailyReportService;

    public DailyReport execute(Long userNo, LocalDate date) {
        return dailyReportService.findByUserNoAndDate(userNo, date);
    }
}
