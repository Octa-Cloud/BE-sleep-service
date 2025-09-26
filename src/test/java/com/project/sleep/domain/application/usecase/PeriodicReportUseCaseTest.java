package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.domain.domain.entity.PeriodicReport;
import com.project.sleep.domain.domain.service.PeriodicReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class PeriodicReportUseCaseTest {

    @Mock
    private PeriodicReportService periodicReportService;

    @InjectMocks
    private PeriodicReportUseCase periodicReportUseCase;

    private PeriodicReport report;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        report = PeriodicReport.builder()
                .periodicReportNo("1")
                .type(PeriodicReport.Type.MONTHLY)
                .date(LocalDate.now())
                .score(85)
                .totalSleepTime(480)
                .bedTime(LocalDateTime.now())
                .deepSleepRatio(0.3)
                .lightSleepRatio(0.5)
                .remSleepRatio(0.2)
                .improvement("더 일찍 자기")
                .weakness("불규칙한 수면")
                .recommendation("수면 패턴 일정 유지")
                .predictDescription("예상 점수는 안정적입니다")
                .scorePrediction(List.of(80, 82, 85))
                .userNo(100L)
                .build();
    }

    @Test
    @DisplayName("리포트가 존재하면 해당 리포트를 반환한다")
    void getPeriodicReportWhenReportExists() {
        // given
        given(periodicReportService.getReport(PeriodicReport.Type.MONTHLY, 100L, LocalDate.now()))
                .willReturn(Optional.of(report));

        // when
        PeriodicReportResponse response =
                periodicReportUseCase.getPeriodicReport(PeriodicReport.Type.MONTHLY, 100L, LocalDate.now());

        // then
        assertThat(response.score()).isEqualTo(85);
        assertThat(response.totalSleepTime()).isEqualTo(480);
        assertThat(response.improvement()).isEqualTo("더 일찍 자기");
    }

    @Test
    @DisplayName("리포트가 존재하지 않으면 emptyResponse를 반환한다")
    void getPeriodicReportWhenReportDoesNotExist() {
        // given
        given(periodicReportService.getReport(PeriodicReport.Type.MONTHLY, 100L, LocalDate.now()))
                .willReturn(Optional.empty());

        // when
        PeriodicReportResponse response =
                periodicReportUseCase.getPeriodicReport(PeriodicReport.Type.MONTHLY, 100L, LocalDate.now());

        // then
        assertThat(response.score()).isEqualTo(0);
        assertThat(response.totalSleepTime()).isEqualTo(0);
        assertThat(response.scorePrediction()).isEmpty();
    }

    @Test
    @DisplayName("Service에서 예외가 발생하면 예외가 전파된다")
    void getPeriodicReportWhenServiceThrowsException() {
        // given
        given(periodicReportService.getReport(PeriodicReport.Type.MONTHLY, 100L, LocalDate.now()))
                .willThrow(new RuntimeException("DB 에러"));

        // when & then
        assertThatThrownBy(() ->
                periodicReportUseCase.getPeriodicReport(PeriodicReport.Type.MONTHLY, 100L, LocalDate.now())
        ).isInstanceOf(RuntimeException.class)
                .hasMessage("DB 에러");
    }

    @Test
    @DisplayName("type이 null이면 예외 발생")
    void getPeriodicReportWhenTypeIsNull() {
        assertThatThrownBy(() -> periodicReportUseCase.getPeriodicReport(null, 1L, LocalDate.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 요청입니다.");
    }// 불필요할수 있지만 혹시 몰라 작성

    @Test
    @DisplayName("date가 null이면 예외 발생")
    void getPeriodicReportWhenDateIsNull() {
        assertThatThrownBy(() -> periodicReportUseCase.getPeriodicReport(PeriodicReport.Type.MONTHLY, 1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 요청입니다.");
    }// 불필요할수 있지만 혹시 몰라 작성

    @Test
    @DisplayName("userNo가 null이면 예외 발생")
    void getPeriodicReportWhenUserNoIsNull() {
        assertThatThrownBy(() -> periodicReportUseCase.getPeriodicReport(PeriodicReport.Type.MONTHLY, null, LocalDate.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("인증이 필요합니다.");
    }
}