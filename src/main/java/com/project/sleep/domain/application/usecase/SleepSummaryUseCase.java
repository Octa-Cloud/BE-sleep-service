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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SleepSummaryUseCase {
    private final DailySleepRecordService dailySleepRecordService;

    // Daily API 로직 (변경 없음)
    public List<SleepSummaryResponse> getDailySummary(LocalDate date) {
        List<DailySleepRecord> records = dailySleepRecordService.getDailySleepRecordsForRecent8Days(date);
        return records.stream()
                .map(SleepSummaryResponse::from)
                .collect(Collectors.toList());
    }

    // Recent API 로직 (변경)
    public SleepSummaryResponse getRecentSummary(LocalDate date) {
        DailySleepRecord record = dailySleepRecordService.getDailySleepRecordByDate(date);

        if (record == null) {
            throw new RestApiException(GlobalErrorStatus._SLEEP_RECORD_NOT_FOUND); // 데이터가 없을 때 예외 발생
        }

        return SleepSummaryResponse.from(record);
    }
}