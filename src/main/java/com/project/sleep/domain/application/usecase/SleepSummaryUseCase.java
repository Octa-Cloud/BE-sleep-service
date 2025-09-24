package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.SleepSummaryResponse;
import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.service.SleepService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SleepSummaryUseCase {

    private final SleepService sleepService;

    // Daily API 로직
    public List<SleepSummaryResponse> getDailySummary(LocalDate date) {
        List<DailySleepRecord> records = sleepService.getDailySleepRecordsForRecent8Days(date);
        return records.stream()
                .map(SleepSummaryResponse::from)
                .collect(Collectors.toList());
    }

    // Recent API 로직
    public SleepSummaryResponse getRecentSummary(LocalDate date) {
        DailySleepRecord record = sleepService.getDailySleepRecordByDate(date);
        if (record == null) {
            return null;
        }
        return SleepSummaryResponse.from(record);
    }
}