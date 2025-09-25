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

    public GlobalErrorStatus getErrorStatus() {
        if (this.errorCode instanceof GlobalErrorStatus) {
            return (GlobalErrorStatus) this.errorCode;
        }
        // 예외가 발생할 경우를 대비해 null 또는 다른 예외를 반환
        return null;
    }
}
