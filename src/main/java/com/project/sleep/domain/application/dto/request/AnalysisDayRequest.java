package com.project.sleep.domain.application.dto.request;

import java.time.LocalDate;

public record AnalysisDayRequest(
        LocalDate date // 조회 기준일 (없으면 기본값==오늘)
) {}
