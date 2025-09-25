package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.GetSleepPatternsResponse;
import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.service.GetSleepPatternsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSleepPatternsUseCase {

    private final GetSleepPatternsService getSleepPatternsService;

    public List<GetSleepPatternsResponse> getSleepPatterns(Long userNo, LocalDate startDate, LocalDate endDate) {

        List<DailySleepRecord> sleepRecords = getSleepPatternsService.getSleepRecords(userNo, startDate, endDate);

        // 빌더, toList 수정
        return sleepRecords.stream()
                .map(record -> GetSleepPatternsResponse.builder()
                        .date(record.getSleepDate())
                        .score(record.getScore())
                        .totalSleepTime(record.getTotalSleepTime())
                        .build())
                .toList();
    }
}
