package com.project.sleep.domain.infra.repository;

import com.project.sleep.domain.domain.entity.DailySleepRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class SleepPatternCustomRepositoryImpl implements SleepPatternCustomRepository {

    private final MongoTemplate mongoTemplate;

    public SleepPatternCustomRepositoryImpl(
            @Qualifier("mongoTemplateRead") MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public List<DailySleepRecord> findByUserNoAndSleepDateBetween(
            Long userNo, LocalDate startInclusive, LocalDate endInclusive) {

        Query query = Query.query(
                Criteria.where("user_no").is(userNo)
                        .and("sleep_date").gte(startInclusive).lte(endInclusive)
        );

        return mongoTemplate.find(query, DailySleepRecord.class);
    }
}