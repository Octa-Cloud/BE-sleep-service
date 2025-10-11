package com.project.sleep.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class ETagGenerator {

    private final ObjectMapper objectMapper;

    /**
     * 객체를 기반으로 ETag 생성
     * MD5 해시를 사용하여 고유한 태그 생성
     */
    public String generate(Object data) {
        try {
            // 객체를 JSON 문자열로 변환 (일관된 직렬화)
            String json = objectMapper.writeValueAsString(data);

            // MD5 해시 생성
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(json.getBytes(StandardCharsets.UTF_8));

            // Base64로 인코딩하여 ETag 형식으로 반환
            String etag = "\"" + Base64.getEncoder().encodeToString(hash) + "\"";

            log.debug("🏷️ ETag generated: {}", etag);
            return etag;

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object for ETag generation", e);
            // 실패 시 타임스탬프 기반 ETag
            return "\"" + System.currentTimeMillis() + "\"";
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5 algorithm not available", e);
            return "\"" + System.currentTimeMillis() + "\"";
        }
    }

    /**
     * 약한 ETag 생성 (W/ 접두사)
     * 의미적으로 동일하지만 바이트 단위로는 다를 수 있는 경우
     */
    public String generateWeak(Object data) {
        String strongETag = generate(data);
        return "W/" + strongETag;
    }
}