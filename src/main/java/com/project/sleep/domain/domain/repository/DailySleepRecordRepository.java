package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailySleepRecordRepository extends MongoRepository<DailySleepRecord, String> {

    // 특정 사용자의 특정 날짜 수면 기록을 Optional로 조회 (프로젝션 적용)
    @Query(value = "{ 'userNo' : ?0, 'sleepDate' : ?1 }", fields = "{ 'sleepDate' : 1, 'score' : 1, 'totalSleepTime' : 1, 'bedTime' : 1, 'wakeTime' : 1 }")
    Optional<DailySleepRecord> findByUserNoAndSleepDate(Long userNo, LocalDate sleepDate);

    // 특정 사용자의 최근 8개 수면 기록 조회 (프로젝션 적용)
    @Query(value = "{ 'userNo' : ?0 }", sort = "{ 'sleepDate' : -1 }", fields = "{ 'sleepDate' : 1, 'score' : 1, 'totalSleepTime' : 1, 'bedTime' : 1, 'wakeTime' : 1 }")
    List<DailySleepRecord> findTop8ByUserNoOrderBySleepDateDesc(Long userNo);

    // 기존 메서드 (사용하지 않음, 필요에 따라 삭제 가능)
    List<DailySleepRecord> findTop8BySleepDateLessThanEqualOrderBySleepDateDesc(LocalDate date);
    DailySleepRecord findBySleepDate(LocalDate date);

}