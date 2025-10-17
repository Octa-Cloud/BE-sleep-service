package com.project.sleep.domain.infra.kafka;

import com.project.sleep.domain.domain.entity.FailureCode;
import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {
    private final FailureCode code;

    public BusinessRuleException(FailureCode code, String message) {
        super(message);
        this.code = code;
    }
    public BusinessRuleException(FailureCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
