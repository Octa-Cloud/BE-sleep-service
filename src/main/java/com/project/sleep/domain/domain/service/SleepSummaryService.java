package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.application.dto.response.SleepSummaryResponse;
import com.project.sleep.domain.application.usecase.SleepSummaryUseCase;
import com.project.sleep.domain.domain.entity.DailySleepRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SleepSummaryService implements SleepSummaryUseCase {

    private final SleepService sleepService;

    @Override
    public List<SleepSummaryResponse> getDailySummary(LocalDate date) {
        List<DailySleepRecord> records = sleepService.getDailySleepRecordsForRecent8Days(date);
        return records.stream()
                .map(SleepSummaryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public SleepSummaryResponse getRecentSummary(LocalDate date) {
        DailySleepRecord record = sleepService.getDailySleepRecordByDate(date);
        if (record == null) {
            return null; // 데이터가 없는 경우
        }
        return SleepSummaryResponse.from(record);
    }
}