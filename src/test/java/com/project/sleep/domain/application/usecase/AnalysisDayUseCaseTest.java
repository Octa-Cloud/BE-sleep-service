package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.domain.service.AnalysisDayService;
import com.project.sleep.global.exception.RestApiException;
import com.project.sleep.global.exception.code.status.GlobalErrorStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisDayUseCaseTest {

    @Mock
    private AnalysisDayService analysisDayService;

    @InjectMocks
    private AnalysisDayUseCase useCase;

    @Test
    void getDailyReportWhenUserAndDateValid() {
        // Given
        Long userNo = 100L;
        LocalDate date = LocalDate.of(2025, 9, 22);
        DailyReport mock = DailyReport.builder()
                .dailyReportId("id-1")
                .userNo(userNo)
                .build();

        when(analysisDayService.findByUserNoAndDate(userNo, date)).thenReturn(mock);

        // When
        DailyReport result = useCase.getDailyAnalysis(userNo, date);

        // Then
        assertThat(result).isSameAs(mock);
        verify(analysisDayService, times(1)).findByUserNoAndDate(userNo, date);
        verifyNoMoreInteractions(analysisDayService);
    }

    @Test
    void getDailyReportWhenDateIsNull() {
        // Given
        Long userNo = 200L;
        DailyReport mock = DailyReport.builder()
                .dailyReportId("id-2")
                .userNo(userNo)
                .build();

        when(analysisDayService.findByUserNoAndDate(eq(userNo), isNull())).thenReturn(mock);

        // When
        DailyReport result = useCase.getDailyAnalysis(userNo, null);

        // Then
        assertThat(result).isSameAs(mock);
        verify(analysisDayService).findByUserNoAndDate(eq(userNo), isNull());
        verifyNoMoreInteractions(analysisDayService);
    }
    @Test
    void getDailyReportWhenServiceThrows() {
        // Given
        Long userNo = 100L;
        LocalDate date = LocalDate.of(2025, 9, 22);

        when(analysisDayService.findByUserNoAndDate(userNo, date))
                .thenThrow(new RestApiException(GlobalErrorStatus._NOT_FOUND));

        // When / Then
        assertThatThrownBy(() -> useCase.getDailyAnalysis(userNo, date))
                .isInstanceOf(RestApiException.class);

        verify(analysisDayService).findByUserNoAndDate(userNo, date);
        verifyNoMoreInteractions(analysisDayService);
    }
}
