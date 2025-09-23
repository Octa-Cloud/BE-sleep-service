package com.project.sleep.domain.application.dto.response;

import com.project.sleep.domain.domain.entity.DailyReport;

import java.util.List;

public record AnalysisDayResponse(
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
        List<Analysis> analysis
) {
    public record Analysis(
            String title,
            String description,
            List<String> steps,
            String difficulty,
            String effect
    ) {}
    // 서비스 코드 간결화를 위해 사용.
    public static AnalysisDayResponse mapToResponse(DailyReport report) {
        return new AnalysisDayResponse(
                report.getDeepSleepTime(),
                report.getLightSleepTime(),
                report.getRemSleepTime(),
                report.getDeepSleepRatio(),
                report.getLightSleepRatio(),
                report.getRemSleepRatio(),
                report.getMemo(),
                report.getMicrowaveGrades(),
                report.getNoiseEventTypes(),
                report.getAnalysis() == null ? List.of() :
                        report.getAnalysis().stream()
                                .map(a->new AnalysisDayResponse.Analysis(
                                        a.getTitle(),
                                        a.getDescription(),
                                        a.getSteps(),
                                        a.getDifficulty(),
                                        a.getEffect()
                                ))
                                .toList()
        );
    }
}
