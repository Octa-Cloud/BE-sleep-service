package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query; // Query import
import java.util.Date;
import java.util.List;

public interface SleepRecordRepository extends MongoRepository<DailySleepRecord, String> {

    // @Query 어노테이션 -> 직접 조회 쿼리 작성 -> 시간대 경계 문제 해결
    @Query("{ 'user_no': ?0, 'sleep_date': { $gte: ?1, $lte: ?2 } }")
    List<DailySleepRecord> findSleepRecordsByDateRange(Long userNo, Date startDate, Date endDate);

}
