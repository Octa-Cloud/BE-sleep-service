package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.SleepSummaryResponse;
import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.service.DailySleepRecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetSleepSummaryUseCaseTest {

    @Mock
    private DailySleepRecordService dailySleepRecordService;

    @InjectMocks
    private GetSleepSummaryUseCase getSleepSummaryUseCase;

    private final Long userNo = 1L;
    private final LocalDate date = LocalDate.of(2023, 10, 26);

    @Test
    @DisplayName("최근 8개 수면 기록 조회 성공")
    void getRecentSummary_success() {
        // Given - 서비스가 8개의 가짜 기록을 반환한다고 가정
        List<DailySleepRecord> mockRecords = IntStream.range(0, 8)
                .mapToObj(i -> DailySleepRecord.builder().userNo(userNo).sleepDate(date.minusDays(i)).build())
                .collect(Collectors.toList());
        given(dailySleepRecordService.getRecent8SleepRecordsByUserNo(userNo)).willReturn(mockRecords);

        // When - 유스케이스 메서드 호출
        List<SleepSummaryResponse> result = getSleepSummaryUseCase.getRecentSummary(userNo);

        // Then - 8개의 응답 객체가 반환되었는지 검증
        assertNotNull(result);
        assertEquals(8, result.size());
    }

    @Test
    @DisplayName("일별 수면 기록 조회 성공")
    void getDailySummary_success() {
        // Given - 서비스가 유효한 단일 기록을 반환한다고 가정
        DailySleepRecord mockRecord = DailySleepRecord.builder().userNo(userNo).sleepDate(date).score(90).build();
        given(dailySleepRecordService.getDailySleepRecordByUserNoAndDate(userNo, date)).willReturn(mockRecord);

        // When - 유스케이스 메서드 호출
        SleepSummaryResponse result = getSleepSummaryUseCase.getDailySummary(userNo, date);

        // Then - 올바른 응답 객체가 반환되었는지 검증
        assertNotNull(result);
        assertEquals(90, result.score());
        assertEquals(date, result.date());
    }

    @Test
    @DisplayName("월별 수면 기록 조회 성공")
    void getMonthlySummary_success() {
        // Given
        Integer year = 2023;
        Integer month = 10;
        List<DailySleepRecord> mockRecords = IntStream.range(1, 6) // 5일간의 기록
                .mapToObj(i -> DailySleepRecord.builder()
                        .userNo(userNo)
                        .sleepDate(LocalDate.of(year, month, i))
                        .score(80 + i)
                        .build())
                .collect(Collectors.toList());
        given(dailySleepRecordService.getMonthlySleepRecordsByUserNo(userNo, year, month)).willReturn(mockRecords);

        // When
        List<SleepSummaryResponse> result = getSleepSummaryUseCase.getMonthlySummary(userNo, year, month);

        // Then
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(81, result.get(0).score()); // 첫 번째 기록의 점수
        assertEquals(LocalDate.of(year, month, 1), result.get(0).date()); // 첫 번째 기록의 날짜
    }
}
