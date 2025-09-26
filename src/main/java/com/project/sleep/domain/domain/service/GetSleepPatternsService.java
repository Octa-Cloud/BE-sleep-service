package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.repository.SleepRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSleepPatternsService {

    private final SleepRecordRepository sleepRecordRepository;

    public List<DailySleepRecord> getSleepRecords(Long userNo, LocalDate startDate, LocalDate endDate) {

        // 한국 시간 기준으로 날짜 변환
        Date startDateTime = Date.from(startDate.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant());
        Date endDateTime = Date.from(endDate.atTime(LocalTime.MAX).atZone(ZoneId.of("Asia/Seoul")).toInstant());

        return sleepRecordRepository.findSleepRecordsByDateRange(userNo, startDateTime, endDateTime);
    }
}
