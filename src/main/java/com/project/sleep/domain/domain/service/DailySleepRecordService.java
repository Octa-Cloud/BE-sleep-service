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
        // 수정된 부분: 고정된 기간이 아닌, 가장 최근 8개의 데이터를 조회합니다.
        return dailySleepRecordRepository.findTop8BySleepDateLessThanEqualOrderBySleepDateDesc(date);
    }

    public DailySleepRecord getDailySleepRecordByDate(LocalDate date) {
        return dailySleepRecordRepository.findBySleepDate(date);
    }
}