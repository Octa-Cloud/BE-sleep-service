package com.project.sleep.domain.application.usecase;

import com.project.sleep.domain.application.dto.response.TotalSleepRecordResponse;
import com.project.sleep.domain.domain.entity.TotalSleepRecord;
import com.project.sleep.domain.domain.service.TotalSleepRecordService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetTotalSleepRecordUseCase {

    private final TotalSleepRecordService totalSleepRecordService;

    @Cacheable(value = "totalRecordCache", key = "#userNo")
    public TotalSleepRecordResponse execute(Long userNo) {
        TotalSleepRecord entity = totalSleepRecordService.findById(userNo);

        TotalSleepRecordResponse response = TotalSleepRecordResponse.from(entity);

        String eTag = "\"" + response.hashCode() + "\"";
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletResponse servletResponse = attrs.getResponse();
            if (servletResponse != null && !servletResponse.containsHeader("ETag")) {
                servletResponse.setHeader("ETag", eTag);
            }
        }
        return response;
    }

    @CacheEvict(value = "totalRecordCache", key = "#userNo")
    public void invalidateWeeklyCache(Long userNo) {
        // 데이터 갱신 후 호출 → 캐시 제거
    }
}
