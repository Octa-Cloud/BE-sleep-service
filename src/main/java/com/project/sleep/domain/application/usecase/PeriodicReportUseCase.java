package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.domain.domain.entity.PeriodicReport;
import com.project.sleep.domain.domain.service.PeriodicReportService;
import com.project.sleep.global.exception.RestApiException;
import com.project.sleep.global.exception.code.BaseCodeInterface;
import com.project.sleep.global.exception.code.status.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class PeriodicReportUseCase {
    private final PeriodicReportService periodicReportService;

    public PeriodicReportResponse getPeriodicReport(PeriodicReport.Type type, Long userNo, LocalDate date){
        if(type == null || date == null){
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
        if(userNo == null){
            throw new IllegalArgumentException("인증이 필요합니다.");
        }
        return periodicReportService.getReport(type, userNo, date)
                .map(PeriodicReportResponse::mapToResponse)
                .orElse(PeriodicReportResponse.emptyResponse());

    }
}
