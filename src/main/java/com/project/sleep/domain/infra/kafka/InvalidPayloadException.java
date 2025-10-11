package com.project.sleep.domain.infra.kafka;

/** 파싱/검증 불가 → @RetryableTopic(exclude) 처리로 즉시 DLT 이동시키기 위한 예외 */
public class InvalidPayloadException extends RuntimeException {
    public InvalidPayloadException(String message) { super(message); }
    public InvalidPayloadException(String message, Throwable cause) { super(message, cause); }
}