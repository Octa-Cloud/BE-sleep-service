package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.repository.DailySleepRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailySleepRecordService {

    private final DailySleepRecordRepository dailySleepRecordRepository;

    public List<DailySleepRecord> getDailySleepRecordsForRecent8Days(LocalDate date) {
        return dailySleepRecordRepository.findTop8BySleepDateLessThanEqualOrderBySleepDateDesc(date);
    }

    public DailySleepRecord getDailySleepRecordByDate(LocalDate date) {
        return dailySleepRecordRepository.findBySleepDate(date);
    }


    public List<DailySleepRecord> getRecent8SleepRecordsByUserNo(Long userNo) {
        return dailySleepRecordRepository.findTop8ByUserNoOrderBySleepDateDesc(userNo);
    }

    public DailySleepRecord getDailySleepRecordByUserNoAndDate(Long userNo, LocalDate date) {
        return dailySleepRecordRepository.findByUserNoAndSleepDate(userNo, date);
    }
}
