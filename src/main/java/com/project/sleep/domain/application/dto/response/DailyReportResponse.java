package com.project.sleep.domain.application.dto.response;

import com.project.sleep.domain.domain.entity.DailyReport;
import lombok.Builder;
import java.util.List;

@Builder
public record DailyReportResponse(
        // 수면 단계별 시간 (분 단위)
        Integer deepSleepTime,
        Integer lightSleepTime,
        Integer remSleepTime,

        // 수면 단계별 비율
        Double deepSleepRatio,
        Double lightSleepRatio,
        Double remSleepRatio,

        // 메모
        String memo,                  // 사용자 메모

        // 뇌파 & 소음 분석
        List<Double> microwaveGrades, // 청크별 뇌파 평균 값 배열
        List<String> noiseEventTypes, // 수면 중 소음 이벤트 유형들

        // 일일 AI 분석 리포트
         String analysisTitle,
         String analysisDescription,
         List<String> analysisSteps,
         String analysisDifficulty,
         String analysisEffect
) {

    // 변환 로직을 DTO 안에만 둔다
    public static DailyReportResponse from(DailyReport report) {
        return DailyReportResponse.builder()
                .deepSleepTime(report.getDeepSleepTime())
                .lightSleepTime(report.getLightSleepTime())
                .remSleepTime(report.getRemSleepTime())
                .deepSleepRatio(report.getDeepSleepRatio())
                .lightSleepRatio(report.getLightSleepRatio())
                .remSleepRatio(report.getRemSleepRatio())
                .memo(report.getMemo())
                .microwaveGrades(report.getMicrowaveGrades())
                .noiseEventTypes(report.getNoiseEventTypes())
                .analysisTitle(report.getAnalysisTitle())
                .analysisDescription(report.getAnalysisDescription())
                .analysisSteps(report.getAnalysisSteps())
                .analysisDifficulty(report.getAnalysisDifficulty())
                .analysisEffect(report.getAnalysisEffect())
                .build();
    }
}