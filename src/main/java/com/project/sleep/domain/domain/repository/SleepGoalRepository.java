package com.project.sleep.domain.domain.repository;

import com.project.sleep.domain.domain.entity.SleepGoal;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SleepGoalRepository extends MongoRepository<SleepGoal, Long> {
}
