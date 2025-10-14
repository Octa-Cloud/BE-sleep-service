package com.project.sleep.domain.application.usecase;

import ch.qos.logback.classic.Logger;
import com.project.sleep.domain.application.dto.response.AnalysisDayResponse;
import com.project.sleep.domain.domain.entity.DailyReport;
import com.project.sleep.domain.domain.service.AnalysisDayService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class GetDailyRecordUseCase {

    private final AnalysisDayService analysisDayService;

    @Cacheable(value = "dailyReportCache", cacheManager = "caffeineCacheManager")
    public DailyReport execute(Long userNo, LocalDate date) {

        DailyReport response = analysisDayService.findByUserNoAndDate(userNo, date);

        String eTag = "\"" + response.hashCode() + "\"";
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletResponse servletResponse = attrs.getResponse();
            if (servletResponse != null) servletResponse.setHeader("ETag", eTag);
        }
        return response;
    }
    @CacheEvict(value = "dailyReportCache", cacheManager = "caffeineCacheManager")
    public void invalidateWeeklyCache(Long userNo, LocalDate startDate) {
        // 데이터 갱신 후 호출 → 캐시 제거
    }
}
