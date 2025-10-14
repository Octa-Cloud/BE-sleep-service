package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.TotalSleepRecordResponse;
import com.project.sleep.domain.domain.entity.TotalSleepRecord;
import com.project.sleep.domain.domain.service.TotalSleepRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetTotalSleepRecordUseCase {

    private final TotalSleepRecordService totalSleepRecordService;

    @Cacheable(value = "totalSleepRecord", key = "#userNo")
    public TotalSleepRecordResponse execute(Long userNo) {
        TotalSleepRecord entity = totalSleepRecordService.findById(userNo);
        return TotalSleepRecordResponse.from(entity);
    }
}
