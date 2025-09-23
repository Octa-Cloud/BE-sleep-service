package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.application.dto.request.PeriodicAnalysisRequest;
import com.project.sleep.domain.application.dto.response.PeriodicAnalysisResponse;
import com.project.sleep.domain.domain.entity.PeriodicReport;
import com.project.sleep.domain.domain.repository.PeriodicReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class PeriodicAnalysisService {

    private final PeriodicReportRepository periodicReportRepository;

    @Transactional
    public PeriodicAnalysisResponse getAnalysis(PeriodicAnalysisRequest request, String userNo) {

        // 날짜 범위 (포함 여부 처리)
        LocalDate startDate = request.getStartDate(); // 하루 전
        LocalDate end = startDate.plusDays(1);    // 하루 후

        System.out.println("Searching with - userNo: " + userNo +
                ", type: " + request.getType() +
                ", start: " + startDate +
                ", end: " + end);

        // 전체 데이터 개수 확인
        long totalCount = periodicReportRepository.count();
        System.out.println("Total records: " + totalCount);

        // 해당 사용자의 모든 데이터 확인
        List<PeriodicReport> allUserRecords = periodicReportRepository.findAll()
                .stream()
                .filter(r -> userNo.equals(r.getUserNo()))
                .collect(toList());
        System.out.println("User records: " + allUserRecords.size());

        List<PeriodicReport> records = periodicReportRepository
                .findByUserNoAndTypeAndDateBetween(userNo, request.getType(), startDate, end);

        System.out.println("Found records: " + records.size());


        if (records.isEmpty()) {
            return PeriodicAnalysisResponse.builder()
                    .avgScore(0)
                    .avgSleepTime(0)
                    .avgBedTime(null)
                    .avgDeepRatio(0)
                    .avgLightRatio(0)
                    .avgRemRatio(0)
                    .improvement("")
                    .weakness("")
                    .recommendation("")
                    .predictDescription("")
                    .scorePrediction(null)
                    .build();
        }

        // 첫 번째 레코드 사용
        PeriodicReport report = records.get(0);

        return PeriodicAnalysisResponse.builder()
                .avgScore(report.getScore())
                .avgSleepTime(report.getTotalSleepTime())
                .avgBedTime(report.getBedTime() != null
                        ? LocalTime.parse(report.getBedTime(), DateTimeFormatter.ofPattern("H:mm"))
                        : null)
                .avgDeepRatio(report.getDeepSleepRatio())
                .avgLightRatio(report.getLightSleepRatio())
                .avgRemRatio(report.getRemSleepRatio())
                .improvement(report.getImprovement())
                .weakness(report.getWeakness())
                .recommendation(report.getRecommendation())
                .predictDescription(report.getScorePrediction() != null ? report.getScorePrediction().getDescription() : "")
                .scorePrediction(report.getScorePrediction() != null ? report.getScorePrediction().getScorePrediction() : null)
                .build();
    }
}
