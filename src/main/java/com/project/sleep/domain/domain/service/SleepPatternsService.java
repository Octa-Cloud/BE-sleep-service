package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.repository.DailySleepRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SleepPatternsService {

    private final DailySleepRecordRepository dailySleepRecordRepository;

    public List<DailySleepRecord> getSleepRecords(Long userNo, LocalDate startDate, LocalDate endDate) {
        return dailySleepRecordRepository.findByUserNoAndSleepDateBetween(userNo, startDate, endDate.plusDays(1));
        // endDate 포함하지 않는 문제 때문에 endDate -> endDate.plusDays(1) 로 수정하여 endDate도 포함할 수 있도록 함
    }
}
