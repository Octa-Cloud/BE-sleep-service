package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.DailyReportResponse;
import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.domain.service.DailyReportService;
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
class GetDailyReportUseCaseTest {

    @Mock
    private DailyReportService dailyReportService;

    @InjectMocks
    private GetDailyReportUseCase useCase;

    @Test
    void getDailyReportWhenUserAndDateValid() {
        // Given
        Long userNo = 100L;
        LocalDate date = LocalDate.of(2025, 9, 22);
        DailyReport mock = DailyReport.builder()
                .dailyReportNo("id-1")
                .userNo(userNo)
                .build();

        when(dailyReportService.findByUserNoAndDate(userNo, date)).thenReturn(mock);

        // When
        DailyReportResponse result = useCase.execute(userNo, date);

        // Then
        assertThat(result).isSameAs(mock);
        verify(dailyReportService, times(1)).findByUserNoAndDate(userNo, date);
        verifyNoMoreInteractions(dailyReportService);
    }

    @Test
    void getDailyReportWhenDateIsNull() {
        // Given
        Long userNo = 200L;
        DailyReport mock = DailyReport.builder()
                .dailyReportNo("id-2")
                .userNo(userNo)
                .build();

        when(dailyReportService.findByUserNoAndDate(eq(userNo), isNull())).thenReturn(mock);

        // When
        DailyReportResponse result = useCase.execute(userNo, null);

        // Then
        assertThat(result).isSameAs(mock);
        verify(dailyReportService).findByUserNoAndDate(eq(userNo), isNull());
        verifyNoMoreInteractions(dailyReportService);
    }
    @Test
    void getDailyReportWhenServiceThrows() {
        // Given
        Long userNo = 100L;
        LocalDate date = LocalDate.of(2025, 9, 22);

        when(dailyReportService.findByUserNoAndDate(userNo, date))
                .thenThrow(new RestApiException(GlobalErrorStatus._NOT_FOUND));

        // When / Then
        assertThatThrownBy(() -> useCase.execute(userNo, date))
                .isInstanceOf(RestApiException.class);

        verify(dailyReportService).findByUserNoAndDate(userNo, date);
        verifyNoMoreInteractions(dailyReportService);
    }
}
