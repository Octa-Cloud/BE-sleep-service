package com.project.sleep.domain.application.dto.response;

import java.time.LocalDate;

// class 대신 record -> 생성자, getter, equals(), hashCode(), toString() 메소드 자동 생성
public record GetSleepPatternsResponse(
        LocalDate date,
        int score,
        int totalSleepTime
) { }