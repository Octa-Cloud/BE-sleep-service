package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.PeriodicReportResponse;
import com.project.sleep.domain.domain.entity.PeriodicReport;
import com.project.sleep.domain.domain.service.PeriodicReportService;
import com.project.sleep.global.util.DateConvertor;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetPeriodicReportUseCase {
    private final PeriodicReportService periodicReportService;

    @Cacheable(value = "weeklyReportCache", key = "#userNo + ':' + #date")
    public PeriodicReportResponse getWeeklyReport(Long userNo, LocalDate date){
        LocalDate start = DateConvertor.weekStart(date);
        LocalDate end = DateConvertor.weekEnd(date);

        PeriodicReportResponse response = periodicReportService.getReport(PeriodicReport.Type.WEEKLY, userNo, start, end)
                .map(PeriodicReportResponse::mapToResponse)
                .orElse(PeriodicReportResponse.emptyResponse());

        // ---------------- ETag 적용 ----------------
        String eTag = "\"" + response.hashCode() + "\"";
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletResponse servletResponse = attrs.getResponse();
            if (servletResponse != null) servletResponse.setHeader("ETag", eTag);
        }

        return response;
    }

    @Cacheable(value = "monthlyReportCache", key = "#userNo + ':' + #date")
    public PeriodicReportResponse getMonthlyReport(Long userNo, LocalDate date){
        LocalDate start = DateConvertor.monthStart(date);
        LocalDate end = DateConvertor.monthEndInclusive(date);

        PeriodicReportResponse response = periodicReportService.getReport(PeriodicReport.Type.MONTHLY, userNo, start, end)
                .map(PeriodicReportResponse::mapToResponse)
                .orElse(PeriodicReportResponse.emptyResponse());

        String eTag = "\"" + response.hashCode() + "\"";
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletResponse servletResponse = attrs.getResponse();
            if (servletResponse != null) servletResponse.setHeader("ETag", eTag);
        }

        return response;
    }

    @CacheEvict(value = "weeklyReportCache", key = "#userNo + ':' + #startDate")
    public void invalidateWeeklyCache(Long userNo, LocalDate startDate) {
        // 데이터 갱신 후 호출 → 캐시 제거
    }
}
