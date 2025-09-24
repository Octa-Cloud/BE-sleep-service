package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.DailyReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Date;
import java.util.Optional;

public interface DailyReportRepository extends MongoRepository<DailyReport, String> {
    // UTC 기준 하루 범위 조회
    @Query("{ 'user_no': ?0, 'sleep_date': { $gte: ?1, $lt: ?2 } }")
    Optional<DailyReport> findOneByUserNoAndSleepDateBetween(Long userNo, Date fromInclusive, Date toExclusive);
}