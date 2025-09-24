package com.project.sleep.domain.application.usecase;


import com.project.sleep.domain.application.dto.response.AnalysisDayResponse;
import com.project.sleep.domain.domain.service.AnalysisDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class  AnalysisDayUseCase {
    private final AnalysisDayService analysisDayService;

    public AnalysisDayResponse getDailyAnalysis(Long userId, LocalDate date) {
        //서비스 호출
        return analysisDayService.getDailyAnalysis(userId, date);
    }

}
