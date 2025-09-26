package com.project.sleep.domain.application.usecase;


import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.domain.service.AnalysisDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class  AnalysisDayUseCase {
    private final AnalysisDayService analysisDayService;

    public DailyReport getDailyAnalysis(Long userNo, LocalDate date) {
        //서비스 호출
        return analysisDayService.findByUserNoAndDate(userNo, date); //findbyusernonddate
        //리턴
        // 응답 엔티티 -> dto ok
        // 응답 dto -> entity no
        // 요청 dto -> entity yes, 택배 박스 속 물품을 까는거
    }

}
