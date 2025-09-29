package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.SleepSummaryResponse;
import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.service.DailySleepRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetSleepSummaryUseCase {
    private final DailySleepRecordService dailySleepRecordService;

    // Daily API 로직
    public SleepSummaryResponse getDailySummary(Long userNo, LocalDate date) {
        DailySleepRecord record = dailySleepRecordService.getDailySleepRecordByUserNoAndDate(userNo, date);
        return SleepSummaryResponse.from(record);
    }

    // Recent API 로직
    public List<SleepSummaryResponse> getRecentSummary(Long userNo) {
        List<DailySleepRecord> records = dailySleepRecordService.getRecent8SleepRecordsByUserNo(userNo);
        return records.stream()
                .map(SleepSummaryResponse::from)
                .collect(Collectors.toList());
    }
}
