package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.domain.entity.TotalSleepRecord;
import com.project.sleep.domain.domain.repository.TotalSleepRecordRepository;
import com.project.sleep.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.project.sleep.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TotalSleepRecordService {

    private final TotalSleepRecordRepository totalSleepRecordRepository;

    public TotalSleepRecord findById(Long userNo) {
        return totalSleepRecordRepository.findById(userNo)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));
    }
}
