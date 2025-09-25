package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.SleepSummaryResponse;
import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.service.DailySleepRecordService;
import com.project.sleep.global.exception.RestApiException;
import com.project.sleep.global.exception.code.status.GlobalErrorStatus;
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
class SleepSummaryUseCaseTest {

    @Mock
    private DailySleepRecordService dailySleepRecordService;

    @InjectMocks
    private SleepSummaryUseCase sleepSummaryUseCase;

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
        List<SleepSummaryResponse> result = sleepSummaryUseCase.getRecentSummary(userNo);

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
        SleepSummaryResponse result = sleepSummaryUseCase.getDailySummary(userNo, date);

        // Then - 올바른 응답 객체가 반환되었는지 검증
        assertNotNull(result);
        assertEquals(90, result.score());
        assertEquals(date, result.date());
    }
}
