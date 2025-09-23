package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.GetSleepPatternsResponse;
import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.repository.SleepRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 전용으로 최적화
public class GetSleepPatternsUseCase {

    private final SleepRecordRepository sleepRecordRepository;

    public List<GetSleepPatternsResponse> getSleepPatterns(LocalDate startDate, LocalDate endDate) {

        List<DailySleepRecord> sleepRecords = sleepRecordRepository.findAllBySleepDateBetween(startDate, endDate);

        return sleepRecords.stream()
                .map(record -> new GetSleepPatternsResponse(
                        record.getSleepDate(),
                        record.getScore(),
                        record.getTotalSleepTime()
                ))
                .collect(Collectors.toList());
    }
}