package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.domain.repository.DailyReportRepository;
import com.project.sleep.global.exception.RestApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisDayServiceTest {

    @Mock
    private DailyReportRepository dailyReportRepository;

    @InjectMocks
    private AnalysisDayService service;

    private Long userNo;

    @BeforeEach
    void setUp() {
        userNo = 123L;
    }

    @Test
    void getDailyReportWhenUserAndDateValid() {
        // Given
        LocalDate date = LocalDate.of(2025, 9, 22);
        Date expectedKey = Date.from(date.atStartOfDay(ZoneOffset.UTC).toInstant());

        DailyReport report = DailyReport.builder()
                .dailyReportId("id-20250922")
                .userNo(userNo)
                .build();

        when(dailyReportRepository.findOneByUserNoAndSleepDate(eq(userNo), eq(expectedKey)))
                .thenReturn(Optional.of(report));

        // When
        DailyReport result = service.findByUserNoAndDate(userNo, date);

        // Then
        assertThat(result).isEqualTo(report);
        verify(dailyReportRepository, times(1))
                .findOneByUserNoAndSleepDate(eq(userNo), eq(expectedKey));
        verifyNoMoreInteractions(dailyReportRepository);
    }

    @Test
    void getDailyReportWhenNotFound() {
        // Given
        LocalDate date = LocalDate.of(2025, 9, 23);
        Date expectedKey = Date.from(date.atStartOfDay(ZoneOffset.UTC).toInstant());

        when(dailyReportRepository.findOneByUserNoAndSleepDate(eq(userNo), eq(expectedKey)))
                .thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.findByUserNoAndDate(userNo, date))
                .isInstanceOf(RestApiException.class);

        verify(dailyReportRepository).findOneByUserNoAndSleepDate(eq(userNo), eq(expectedKey));
        verifyNoMoreInteractions(dailyReportRepository);
    }

    @Test
    void getDailyReportWhenDateIsNull() {
        // Given
        DailyReport report = DailyReport.builder()
                .dailyReportId("id-today")
                .userNo(userNo)
                .build();

        when(dailyReportRepository.findOneByUserNoAndSleepDate(eq(userNo), any(Date.class)))
                .thenReturn(Optional.of(report));

        // When
        DailyReport result = service.findByUserNoAndDate(userNo, null);

        // Then
        assertThat(result).isEqualTo(report);
        verify(dailyReportRepository, times(1))
                .findOneByUserNoAndSleepDate(eq(userNo), any(Date.class));
        verifyNoMoreInteractions(dailyReportRepository);
    }

    @Test
    void getDailyReportWhenRepositoryThrows() {
        // Given
        Long userNo = 123L;
        LocalDate date = LocalDate.of(2025, 9, 22);
        Date expectedKey = Date.from(date.atStartOfDay(ZoneOffset.UTC).toInstant());

        when(dailyReportRepository.findOneByUserNoAndSleepDate(eq(userNo), eq(expectedKey)))
                .thenThrow(new RuntimeException("repo boom"));

        // When / Then
        assertThatThrownBy(() -> service.findByUserNoAndDate(userNo, date))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("repo boom");

        verify(dailyReportRepository).findOneByUserNoAndSleepDate(eq(userNo), eq(expectedKey));
        verifyNoMoreInteractions(dailyReportRepository);
    }
}