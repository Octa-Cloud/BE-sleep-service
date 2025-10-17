package com.project.sleep.domain.infra.repository;

import com.project.sleep.domain.domain.entity.PeriodicReport;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Repository
public class PeriodicReportCustomRepositoryImpl implements PeriodicReportCustomRepository {

    private final MongoTemplate mongoTemplate;

    public PeriodicReportCustomRepositoryImpl(
            @Qualifier("mongoTemplateRead") MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public Optional<PeriodicReport> findOneByUserNoAndTypeAndDateBetween(
            Long userNo, PeriodicReport.Type type, LocalDate startInclusive, LocalDate endInclusive) {

        ZoneId kst = ZoneId.of("Asia/Seoul");
        Date startDate = Date.from(startInclusive.atStartOfDay(kst).toInstant());
        Date endDate = Date.from(endInclusive.plusDays(1).atStartOfDay(kst).toInstant());

        Criteria c = Criteria.where("user_no").is(userNo)
                .and("type").is(type)
                .and("start_date").is(startDate)
                .and("end_date").is(endDate);

        Query q = new Query(c).limit(1);
        PeriodicReport hit = mongoTemplate.findOne(q, PeriodicReport.class);
        return Optional.ofNullable(hit);
    }
}
