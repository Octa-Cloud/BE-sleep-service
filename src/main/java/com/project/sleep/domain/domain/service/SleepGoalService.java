package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.application.dto.request.SleepGoalRequest;
import com.project.sleep.domain.domain.entity.SleepGoal;
import com.project.sleep.domain.domain.repository.SleepGoalRepository;
import com.project.sleep.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.project.sleep.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SleepGoalService {

    private final SleepGoalRepository sleepGoalRepository;

    public void upsert(Long userNo, SleepGoalRequest request) {
        sleepGoalRepository.findById(userNo)
                .map(goal -> {
                    goal.update(request.goalBedTime(), request.goalWakeTime(), request.goalTotalSleepTime());
                    return sleepGoalRepository.save(goal);
                })
                .orElseGet(() -> sleepGoalRepository.save(
                        SleepGoal.builder()
                                .userNo(userNo)
                                .goalBedTime(request.goalBedTime())
                                .goalWakeTime(request.goalWakeTime())
                                .goalTotalSleepTime(request.goalTotalSleepTime())
                                .build()
                ));
    }

    public SleepGoal findById(Long userNo) {
        return sleepGoalRepository.findById(userNo)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));
    }
}
