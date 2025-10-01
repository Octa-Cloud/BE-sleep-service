package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.SleepGoalResponse;
import com.project.sleep.domain.domain.entity.SleepGoal;
import com.project.sleep.domain.domain.service.SleepGoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GetSleepGoalUseCaseTest {

    private SleepGoalService sleepGoalService;
    private GetSleepGoalUseCase useCase;

    @BeforeEach
    void setUp() {
        sleepGoalService = mock(SleepGoalService.class);
        useCase = new GetSleepGoalUseCase(sleepGoalService);
    }

    @Test
    @DisplayName("DB에서 수면 목표를 조회하면 Response로 변환된다")
    void execute_shouldReturnSleepGoalResponse() {
        Long userNo = 1L;
        SleepGoal entity = SleepGoal.builder()
                .userNo(userNo)
                .goalBedTime(LocalTime.of(23, 0))
                .goalWakeTime(LocalTime.of(7, 0))
                .goalTotalSleepTime(8)
                .build();

        when(sleepGoalService.findById(userNo)).thenReturn(entity);

        SleepGoalResponse response = useCase.execute(userNo);

        assertThat(response.goalBedTime()).isEqualTo("23:00");
        assertThat(response.goalWakeTime()).isEqualTo("07:00");
        assertThat(response.goalTotalSleepTime()).isEqualTo(8);

        verify(sleepGoalService, times(1)).findById(userNo);
        verifyNoMoreInteractions(sleepGoalService);
    }
}