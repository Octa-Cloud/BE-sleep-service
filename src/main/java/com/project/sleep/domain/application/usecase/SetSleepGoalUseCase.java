package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.request.SetSleepGoalRequest;
import com.project.sleep.domain.domain.service.SleepGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SetSleepGoalUseCase {

    private final SleepGoalService sleepGoalService;

    public void execute(Long userNo, SetSleepGoalRequest request) {
        sleepGoalService.upsert(userNo, request);
    }
}
