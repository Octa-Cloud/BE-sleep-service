package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.SleepGoalResponse;
import com.project.sleep.domain.domain.entity.SleepGoal;
import com.project.sleep.domain.domain.service.SleepGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

@Service
@RequiredArgsConstructor
public class GetSleepGoalUseCase {

    private final SleepGoalService sleepGoalService;

    @Cacheable(cacheNames = "sleep-goal", key = "#userNo")
    public SleepGoalResponse execute(Long userNo) {
        SleepGoal entity = sleepGoalService.findById(userNo);
        return SleepGoalResponse.from(entity);
    }
}
