package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.repository.DailySleepRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // Optional 임포트 추가

@Service
@RequiredArgsConstructor
public class DailySleepRecordService {

    private final DailySleepRecordRepository dailySleepRecordRepository;

    public List<DailySleepRecord> getDailySleepRecordsForRecent8Days(LocalDate date) {
        // 최근 8개의 데이터를 조회
        return dailySleepRecordRepository.findTop8BySleepDateLessThanEqualOrderBySleepDateDesc(date);
    }

    public DailySleepRecord getDailySleepRecordByDate(LocalDate date) {
        return dailySleepRecordRepository.findBySleepDate(date);
    }

    // 특정 사용자의 최근 8개 수면 기록 조회
    public List<DailySleepRecord> getRecent8SleepRecordsByUserNo(Long userNo) {
        return dailySleepRecordRepository.findTop8ByUserNoOrderBySleepDateDesc(userNo);
    }

    // 특정 사용자의 특정 날짜 수면 기록을 Optional로 조회
    public Optional<DailySleepRecord> getDailySleepRecordByUserNoAndDate(Long userNo, LocalDate date) {
        return dailySleepRecordRepository.findByUserNoAndSleepDate(userNo, date);
    }
}
