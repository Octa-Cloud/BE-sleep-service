package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.SleepSummaryResponse;
import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.service.DailySleepRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetSleepSummaryUseCase {
    private final DailySleepRecordService dailySleepRecordService;

    @Cacheable(value = "dailySleepSummary", key = "#userNo + '_' + #date")
    public SleepSummaryResponse getDailySummary(Long userNo, LocalDate date) {
        System.out.println("🔥🔥🔥 [CACHE MISS] DB 조회 발생 - userNo: " + userNo + ", date: " + date);
        log.warn("🔥 [CACHE MISS] DB 조회 발생 - userNo: {}, date: {}", userNo, date);
        DailySleepRecord record = dailySleepRecordService.getDailySleepRecordByUserNoAndDate(userNo, date);
        return SleepSummaryResponse.from(record);
    }

    @Cacheable(value = "recentSleepSummary", key = "#userNo")
    public List<SleepSummaryResponse> getRecentSummary(Long userNo) {
        System.out.println("🔥🔥🔥 [CACHE MISS] DB 조회 발생 - userNo: " + userNo);
        log.warn("🔥 [CACHE MISS] DB 조회 발생 - userNo: {}", userNo);
        List<DailySleepRecord> records = dailySleepRecordService.getRecent8SleepRecordsByUserNo(userNo);
        return records.stream()
                .map(SleepSummaryResponse::from)
                .collect(Collectors.toList());
    }
}
