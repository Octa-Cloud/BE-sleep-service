package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.SleepPatternsResponse;
import com.project.sleep.domain.domain.service.SleepPatternsService;
import com.project.sleep.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.util.List;

import static com.project.sleep.global.exception.code.status.GlobalErrorStatus.DATE_INVALID_ARGUMENT;

@Service
@RequiredArgsConstructor
public class GetSleepPatternsUseCase {

    private final SleepPatternsService getSleepPatternsService;

    // key: userNo, startDate, endDate가 모두 같아야 같은 캐시로 취급하도록 동적 키 생성
    @Cacheable(cacheNames = "sleep-patterns",
            key = "#userNo.toString() + ':' + #startDate.toString() + ':' + #endDate.toString()")
    public List<SleepPatternsResponse> getSleepPatterns(Long userNo, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate))
            throw new RestApiException(DATE_INVALID_ARGUMENT);

        return getSleepPatternsService.getSleepRecords(userNo, startDate, endDate).stream()
                .map(record -> SleepPatternsResponse.builder()
                        .date(record.getSleepDate())
                        .score(record.getScore())
                        .totalSleepTime(record.getTotalSleepTime())
                        .build())
                .toList();
    }
}
