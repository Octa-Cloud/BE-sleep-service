package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.SleepGoalResponse;
import com.project.sleep.domain.domain.entity.SleepGoal;
import com.project.sleep.domain.domain.service.SleepGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSleepGoalUseCase {

    private final SleepGoalService sleepGoalService;

    public SleepGoalResponse execute(Long userNo) {
        SleepGoal entity = sleepGoalService.findByUserNo(userNo);
        return SleepGoalResponse.from(entity);
    }
}
