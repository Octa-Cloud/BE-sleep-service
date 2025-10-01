package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.SleepPatternsResponse;
import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.service.SleepPatternsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // JUnit5 + Mockito 통합
class GetSleepPatternsUseCaseTest {

    @Mock
    private SleepPatternsService getSleepPatternsService;

    @InjectMocks
    private GetSleepPatternsUseCase getSleepPatternsUseCase;

    // ================= 1. 정상 시나리오 ===============================
    @Nested
    @DisplayName("정상 시나리오")
    class NormalScenario {

        // 1. 정상 시나리오(1) : 데이터가 있을 때
        @Test
        @DisplayName("유저의 수면 기록이 존재하면 DTO 리스트로 변환한다")
        void getDailySleepRecordWhenRecordExists() {
            // Given
            Long userNo = 1L;
            LocalDate start = LocalDate.of(2025, 9, 1);
            LocalDate end = LocalDate.of(2025, 9, 7);

            DailySleepRecord record = createSleepRecord(userNo, start, 85, 480);

            when(getSleepPatternsService.getSleepRecords(userNo, start, end))
                    .thenReturn(List.of(record));

            // When
            List<SleepPatternsResponse> result =
                    getSleepPatternsUseCase.getSleepPatterns(userNo, start, end);

            // Then
            assertThat(result).hasSize(1); // 반환된 리스트 크기 1개
            assertThat(result.get(0).date()).isEqualTo(record.getSleepDate()); // dto 필드 값 date가 entity와 일치
            assertThat(result.get(0).score()).isEqualTo(record.getScore()); // dto 필드 값 score가 entity와 일치
            assertThat(result.get(0).totalSleepTime()).isEqualTo(record.getTotalSleepTime()); // dto 필드 값 totalSleepTime이 entity와 일치

            verify(getSleepPatternsService, times(1)).getSleepRecords(userNo, start, end); // Service 1번 호출
        }

        // 1. 정상 시나리오(2) : 데이터가 없을 때
        @Test
        @DisplayName("조회된 기록이 없으면 빈 리스트 반환")
        void getDailySleepRecordWhenNoRecords() {
            // Given
            Long userNo = 1L;
            LocalDate start = LocalDate.of(2025, 9, 1);
            LocalDate end = LocalDate.of(2025, 9, 7);

            when(getSleepPatternsService.getSleepRecords(userNo, start, end))
                    .thenReturn(Collections.emptyList());

            // When
            List<SleepPatternsResponse> result =
                    getSleepPatternsUseCase.getSleepPatterns(userNo, start, end);

            // Then
            assertThat(result).isEmpty(); // 반환값 빈 리스트인지
            verify(getSleepPatternsService, times(1)).getSleepRecords(userNo, start, end); // Service 1번 호출
        }
    }

    // ================= 2. 에러 시나리오 ===============================v
    @Nested
    @DisplayName("에러 시나리오")
    class ErrorScenario {

        // 2. 에러 시나리오 - Service에서 예외 발생
        @Test
        @DisplayName("Service 계층에서 예외 발생 시 그대로 전파한다")
        void getDailySleepRecordWhenServiceThrowsException() {
            // Given
            Long userNo = 1L;
            LocalDate start = LocalDate.of(2025, 9, 1);
            LocalDate end = LocalDate.of(2025, 9, 7);

            when(getSleepPatternsService.getSleepRecords(userNo, start, end))
                    .thenThrow(new RuntimeException("DB error"));

            // When & Then
            assertThatThrownBy(() ->
                    getSleepPatternsUseCase.getSleepPatterns(userNo, start, end)
            ).isInstanceOf(RuntimeException.class)
                    .hasMessage("DB error");

            verify(getSleepPatternsService, times(1)).getSleepRecords(userNo, start, end); // Service 1번 호출
        }
    }

    // ================= 3. 경계값 시나리오 ===============================
    @Nested
    @DisplayName("경계값 시나리오")
    class BoundaryScenario {

        // 3. 계값 시나리오(1) : startDate = endDate
        @Test
        @DisplayName("startDate = endDate 인 경우 단일 날짜 조회 가능")
        void getDailySleepRecordWhenStartDateEqualsEndDate() {
            // Given
            Long userNo = 1L;
            LocalDate date = LocalDate.of(2025, 9, 1);

            DailySleepRecord record = createSleepRecord(userNo, date, 70, 480);

            when(getSleepPatternsService.getSleepRecords(userNo, date, date))
                    .thenReturn(List.of(record));

            // When
            List<SleepPatternsResponse> result =
                    getSleepPatternsUseCase.getSleepPatterns(userNo, date, date);

            // Then
            assertThat(result).hasSize(1); // 반환 리스트 크기 (1개)
            assertThat(result.get(0).date()).isEqualTo(date); // 결과 dto date가 입력한 날짜와 동일

            verify(getSleepPatternsService, times(1)).getSleepRecords(userNo, date, date); // Service 1번 호출됐는지
        }

        // 3. 경계값 시나리오(2) : startDate > endDate
        @Test
        @DisplayName("startDate > endDate 인 경우 IllegalArgumentException 발생")
        void getDailySleepRecordWhenInvalidDateRange() {
            // Given
            Long userNo = 1L;
            LocalDate start = LocalDate.of(2025, 9, 10);
            LocalDate end = LocalDate.of(2025, 9, 1);

            // When & Then
            assertThatThrownBy(() ->
                    getSleepPatternsUseCase.getSleepPatterns(userNo, start, end)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("startDate must be before endDate");

            // Service는 호출되지 않아야 함
            verify(getSleepPatternsService, never()).getSleepRecords(any(), any(), any());
        }
    }

    // Helper Method
    private DailySleepRecord createSleepRecord(Long userNo, LocalDate date, int score, int totalSleepTime) {
        return DailySleepRecord.builder()
                .dailySleepRecordNo("test-" + date)
                .userNo(userNo)
                .sleepDate(date)
                .score(score)
                .bedTime(LocalTime.of(23, 0))
                .wakeTime(LocalTime.of(7, 0))
                .totalSleepTime(totalSleepTime)
                .build();
    }
}
