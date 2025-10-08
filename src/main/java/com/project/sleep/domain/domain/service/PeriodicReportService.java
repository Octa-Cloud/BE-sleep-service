package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.domain.entity.PeriodicReport;
import com.project.sleep.domain.domain.repository.PeriodicReportRepository;
import com.project.sleep.global.util.DateConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeriodicReportService {

    private final PeriodicReportRepository periodicReportRepository;

    public Optional<PeriodicReport> getReport(PeriodicReport.Type type, Long userNo, LocalDate start, LocalDate end) {
        return periodicReportRepository
                .findOneByUserNoAndTypeAndDateBetween(userNo, type, start, end);
    }
}