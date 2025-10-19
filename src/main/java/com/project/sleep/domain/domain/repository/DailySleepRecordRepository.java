package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import com.project.sleep.domain.infra.repository.SleepPatternCustomRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailySleepRecordRepository extends MongoRepository<DailySleepRecord, String>, SleepPatternCustomRepository {

    // 특정 사용자의 특정 날짜 수면 기록을 Optional로 조회 (프로젝션 적용x)
    @Query(value = "{ 'userNo' : ?0, 'sleepDate' : ?1 }")
    Optional<DailySleepRecord> findByUserNoAndSleepDate(Long userNo, LocalDate sleepDate);

    // 특정 사용자의 최근 8개 수면 기록 조회 (프로젝션 적용x)
    // 쿼리 메서드 규칙과 @Query 어노테이션 충돌 이슈(limit(8)를 무시함)로 제거
    //@Query(value = "{ 'userNo' : ?0 }", sort = "{ 'sleepDate' : -1 }", fields = "{ 'sleepDate' : 1, 'score' : 1, 'totalSleepTime' : 1, 'bedTime' : 1, 'wakeTime' : 1 }")
    List<DailySleepRecord> findTop8ByUserNoOrderBySleepDateDesc(Long userNo);

    // 특정 사용자의 특정 년월 수면 기록 조회
    @Query(value = "{ 'userNo' : ?0, 'sleepDate' : { $gte: ?1, $lt: ?2 } }", sort = "{ 'sleepDate' : 1 }")
    List<DailySleepRecord> findByUserNoAndSleepDateBetween(Long userNo, LocalDate startDate, LocalDate endDate);

}