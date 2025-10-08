package com.project.sleep.domain.infra.repository;

import com.project.sleep.domain.domain.entity.PeriodicReport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PeriodicReportCustomRepositoryImpl implements PeriodicReportCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<PeriodicReport> findOneByUserNoAndTypeAndDateBetween(
            Long userNo, PeriodicReport.Type type, LocalDate startInclusive, LocalDate endInclusive) {

        Criteria c = Criteria.where("userNo").is(userNo)
                .and("type").is(type)
                .and("start_date").is(startInclusive)
                .and("end_date").is(endInclusive);

        Query q = new Query(c).limit(1);
        PeriodicReport hit = mongoTemplate.findOne(q, PeriodicReport.class);
        return Optional.ofNullable(hit);
    }
}
