package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.SleepSummaryResponse;
import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.service.DailySleepRecordService;
import com.project.sleep.global.exception.RestApiException;
import com.project.sleep.global.exception.code.status.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SleepSummaryUseCase {
    private final DailySleepRecordService dailySleepRecordService;

    // Daily API 로직
    public List<SleepSummaryResponse> getDailySummary(Long userNo, LocalDate date) {
        DailySleepRecord record = dailySleepRecordService.getDailySleepRecordByUserNoAndDate(userNo, date)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._SLEEP_RECORD_NOT_FOUND));

        return Collections.singletonList(SleepSummaryResponse.from(record));
    }

    // Recent API 로직
    public List<SleepSummaryResponse> getRecentSummary(Long userNo) {
        List<DailySleepRecord> records = dailySleepRecordService.getRecent8SleepRecordsByUserNo(userNo);

        return records.stream()
                .map(SleepSummaryResponse::from)
                .collect(Collectors.toList());
    }
}
