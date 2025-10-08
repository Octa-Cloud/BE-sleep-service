package com.project.sleep.global.exception;

import com.project.sleep.global.exception.code.BaseCode;
import com.project.sleep.global.exception.code.BaseCodeInterface;
import com.project.sleep.global.exception.code.status.GlobalErrorStatus;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RestApiException extends RuntimeException {

    private final BaseCodeInterface errorCode;

    public BaseCode getErrorCode() {
        return this.errorCode.getCode();
    }
}
