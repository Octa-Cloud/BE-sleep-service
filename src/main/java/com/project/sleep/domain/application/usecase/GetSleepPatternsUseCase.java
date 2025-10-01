package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.SleepPatternsResponse;
import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.service.SleepPatternsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSleepPatternsUseCase {

    private final SleepPatternsService getSleepPatternsService;

    public List<SleepPatternsResponse> getSleepPatterns(Long userNo, LocalDate startDate, LocalDate endDate) {

        // 비즈니스 규칙: startDate는 endDate보다 이후일 수 없음
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }

        List<DailySleepRecord> sleepRecords = getSleepPatternsService.getSleepRecords(userNo, startDate, endDate);

        // 빌더, toList 수정
        return sleepRecords.stream()
                .map(record -> SleepPatternsResponse.builder()
                        .date(record.getSleepDate())
                        .score(record.getScore())
                        .totalSleepTime(record.getTotalSleepTime())
                        .build())
                .toList();
    }
}
