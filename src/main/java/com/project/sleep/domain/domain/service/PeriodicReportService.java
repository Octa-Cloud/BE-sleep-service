package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.domain.domain.entity.PeriodicReport;
import com.project.sleep.domain.domain.repository.PeriodicReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PeriodicReportService {

    private final PeriodicReportRepository periodicReportRepository;
    public PeriodicReportResponse getAnalysis(String type, Long userNo, LocalDate date) {

        // 날짜 범위 (포함 여부 처리)
        LocalDate start = date;
        LocalDate end = start.plusDays(1);    // 하루 후


        PeriodicReport records = periodicReportRepository
                .findByUserNoAndTypeAndDateBetween(userNo, type, start, end);

        if(records == null){
            return PeriodicReportResponse.emptyResponse();
        }

        return PeriodicReportResponse.mapToResponse(records);
    }
}
