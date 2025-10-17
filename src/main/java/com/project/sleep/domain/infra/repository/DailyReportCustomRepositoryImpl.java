package com.project.sleep.domain.infra.repository;

import com.project.sleep.domain.domain.entity.DailyReport;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public class DailyReportCustomRepositoryImpl implements DailyReportCustomRepository {

    private final MongoTemplate mongoTemplate;

    public DailyReportCustomRepositoryImpl(
            @Qualifier("mongoTemplateRead") MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<DailyReport> findOneByUserNoAndSleepDateRange(
            Long userNo, LocalDate startInclusive, LocalDate endExclusive) {

        Query query = Query.query(
                Criteria.where("user_no").is(userNo)
                        .and("sleep_date").gte(startInclusive).lt(endExclusive)
        ).limit(1);

        DailyReport one = mongoTemplate.findOne(query, DailyReport.class);
        return Optional.ofNullable(one);
    }
}