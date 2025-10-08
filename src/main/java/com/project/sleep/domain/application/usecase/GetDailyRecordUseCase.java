package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.domain.service.AnalysisDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class GetDailyRecordUseCase {

    private final AnalysisDayService analysisDayService;

    public DailyReport execute(Long userNo, LocalDate date) {
        return analysisDayService.findByUserNoAndDate(userNo, date);
    }
}
