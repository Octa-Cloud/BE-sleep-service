package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.SleepGoal;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SleepGoalRepository extends MongoRepository<SleepGoal, String> {
    Optional<SleepGoal> findByUserNo(Long userNo);
}
