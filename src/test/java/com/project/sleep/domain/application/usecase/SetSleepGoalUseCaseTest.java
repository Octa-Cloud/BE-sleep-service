package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.request.SleepGoalRequest;
import com.project.sleep.domain.domain.service.SleepGoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SetSleepGoalUseCaseTest {

    private SleepGoalService sleepGoalService;
    private SetSleepGoalUseCase useCase;

    @BeforeEach
    void setUp() {
        sleepGoalService = mock(SleepGoalService.class);
        useCase = new SetSleepGoalUseCase(sleepGoalService);
    }

    @Test
    @DisplayName("DB에 값X Insert")
    void execute_shouldInsertWhenDataDoesNotExist() {
        Long userNo = 1L;
        SleepGoalRequest request = new SleepGoalRequest(LocalTime.of(23, 0), LocalTime.of(7, 0), 8);

        useCase.execute(userNo, request);

        ArgumentCaptor<Long> userNoCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<SleepGoalRequest> reqCaptor = ArgumentCaptor.forClass(SleepGoalRequest.class);
        verify(sleepGoalService, times(1)).upsert(userNoCaptor.capture(), reqCaptor.capture());
        assertThat(userNoCaptor.getValue()).isEqualTo(userNo);
        assertThat(reqCaptor.getValue()).isEqualTo(request);
        verifyNoMoreInteractions(sleepGoalService);
    }

    @Test
    @DisplayName("DB에 값O Update")
    void execute_shouldUpdateWhenDataAlreadyExists() {
        Long userNo = 2L;
        SleepGoalRequest request = new SleepGoalRequest(LocalTime.of(0, 0), LocalTime.of(8, 0), 9);

        useCase.execute(userNo, request);

        ArgumentCaptor<Long> userNoCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<SleepGoalRequest> reqCaptor = ArgumentCaptor.forClass(SleepGoalRequest.class);
        verify(sleepGoalService, times(1)).upsert(userNoCaptor.capture(), reqCaptor.capture());
        assertThat(userNoCaptor.getValue()).isEqualTo(userNo);
        assertThat(reqCaptor.getValue()).isEqualTo(request);
        verifyNoMoreInteractions(sleepGoalService);
    }
}
