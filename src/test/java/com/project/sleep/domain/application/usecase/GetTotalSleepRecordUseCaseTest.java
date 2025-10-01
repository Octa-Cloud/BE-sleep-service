package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.TotalSleepRecordResponse;
import com.project.sleep.domain.domain.entity.TotalSleepRecord;
import com.project.sleep.domain.domain.service.TotalSleepRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GetTotalSleepRecordUseCaseTest {

    private TotalSleepRecordService totalSleepRecordService;
    private GetTotalSleepRecordUseCase useCase;

    @BeforeEach
    void setUp() {
        totalSleepRecordService = mock(TotalSleepRecordService.class);
        useCase = new GetTotalSleepRecordUseCase(totalSleepRecordService);
    }

    @Test
    @DisplayName("DB에서 총 수면 기록을 조회하면 Response로 변환된다")
    void execute_shouldReturnTotalSleepRecordResponse() {
        Long userNo = 1L;

        TotalSleepRecord entity = TotalSleepRecord.builder()
                .userNo(userNo)
                .avgScore(85)
                .avgBedTime(LocalTime.of(23, 0))
                .avgSleepTime(480)
                .build();

        when(totalSleepRecordService.findById(userNo)).thenReturn(entity);

        TotalSleepRecordResponse response = useCase.execute(userNo);

        assertThat(response.avgScore()).isEqualTo(85);
        assertThat(response.avgBedTime()).isEqualTo(LocalTime.of(23, 0));
        assertThat(response.avgSleepTime()).isEqualTo(480);

        verify(totalSleepRecordService, times(1)).findById(userNo);
        verifyNoMoreInteractions(totalSleepRecordService);
    }
}
