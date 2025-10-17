package com.project.sleep.domain.domain.entity;

public enum FailureCode {
    DELETE_GUARD_ENABLED,     // 운영 보호 스위치로 삭제 금지
    CONSISTENCY_MISMATCH,     // 이동/삭제 개수 불일치
    TOO_MANY_DOCS,            // 임계 상한 초과
    USER_NOT_FOUND_ACTIVE,    // 삭제할 활성 데이터 없음 (정책에 따라 FAIL or 성공-무해)
    COMPENSATE_SOURCE_MISSING // 복구 소스(_archive) 부재
}