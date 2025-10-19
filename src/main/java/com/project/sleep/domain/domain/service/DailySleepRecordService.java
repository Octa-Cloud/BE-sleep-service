package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.repository.DailySleepRecordRepository;
import com.project.sleep.global.exception.RestApiException;
import com.project.sleep.global.exception.code.status.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DailySleepRecordService {

    private final DailySleepRecordRepository dailySleepRecordRepository;

    public List<DailySleepRecord> getRecent8SleepRecordsByUserNo(Long userNo) {
        return dailySleepRecordRepository.findTop8ByUserNoOrderBySleepDateDesc(userNo);
    }

    public DailySleepRecord getDailySleepRecordByUserNoAndDate(Long userNo, LocalDate date) {
        return dailySleepRecordRepository.findByUserNoAndSleepDate(userNo, date)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._SLEEP_RECORD_NOT_FOUND));
    }

    public List<DailySleepRecord> getMonthlySleepRecordsByUserNo(Long userNo, Integer year, Integer month) {
        // 해당 월의 시작일과 다음 달 첫째 날 계산
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        
        return dailySleepRecordRepository.findByUserNoAndSleepDateBetween(userNo, startDate, endDate);
    }
}
