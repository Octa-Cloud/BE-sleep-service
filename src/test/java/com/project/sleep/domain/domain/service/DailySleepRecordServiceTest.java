package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.domain.repository.DailySleepRecordRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DailySleepRecordServiceTest {

    @Mock
    private DailySleepRecordRepository dailySleepRecordRepository;

    @InjectMocks
    private DailySleepRecordService dailySleepRecordService;

    private final Long userNo = 1L;
    private final LocalDate date = LocalDate.of(2023, 10, 26);

    @Test
    @DisplayName("특정 날짜 수면 기록 조회 성공 - 정상 케이스")
    void getDailySleepRecordByUserNoAndDate_success() {
        // Given - Repository가 유효한 기록을 Optional로 반환한다고 가정
        DailySleepRecord mockRecord = DailySleepRecord.builder()
                .userNo(userNo)
                .sleepDate(date)
                .build();
        given(dailySleepRecordRepository.findByUserNoAndSleepDate(userNo, date))
                .willReturn(Optional.of(mockRecord));

        // When - 서비스 메서드 호출
        DailySleepRecord result = dailySleepRecordService.getDailySleepRecordByUserNoAndDate(userNo, date);

        // Then - NPE 없이 결과가 반환되었는지 검증
        assertNotNull(result);
        assertEquals(userNo, result.getUserNo());
        assertEquals(date, result.getSleepDate());
    }

    @Test
    @DisplayName("특정 날짜 수면 기록 조회 실패 - 기록이 없는 경우")
    void getDailySleepRecordByUserNoAndDate_failure_notFound() {
        // Given - Repository가 빈 Optional을 반환한다고 가정
        given(dailySleepRecordRepository.findByUserNoAndSleepDate(userNo, date))
                .willReturn(Optional.empty());

        // When & Then - RestApiException이 발생하는지 검증
        RestApiException thrown = assertThrows(RestApiException.class,
                () -> dailySleepRecordService.getDailySleepRecordByUserNoAndDate(userNo, date));
        assertEquals(GlobalErrorStatus._SLEEP_RECORD_NOT_FOUND, thrown.getErrorCode());
    }

    @Test
    @DisplayName("최근 8개 수면 기록 조회 성공 - 정상 케이스")
    void getRecent8SleepRecordsByUserNo_success() {
        // Given - Repository가 2개의 가짜 기록 리스트를 반환한다고 가정
        List<DailySleepRecord> mockRecords = List.of(
                DailySleepRecord.builder().sleepDate(LocalDate.now().minusDays(1)).build(),
                DailySleepRecord.builder().sleepDate(LocalDate.now().minusDays(2)).build()
        );
        given(dailySleepRecordRepository.findTop8ByUserNoOrderBySleepDateDesc(userNo))
                .willReturn(mockRecords);

        // When - 서비스 메서드 호출
        List<DailySleepRecord> result = dailySleepRecordService.getRecent8SleepRecordsByUserNo(userNo);

        // Then - 예상한 크기의 리스트가 반환되었는지 검증
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }
}
