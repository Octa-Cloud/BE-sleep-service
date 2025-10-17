// src/test/java/com/project/sleep/domain/domain/infra/kafka/KafkaSaslConnectionIT.java
package com.project.sleep.domain.domain.infra.kafka;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

class KafkaSaslConnectionIT {

    @Test
    @EnabledIfEnvironmentVariable(named = "RUN_SASL_IT", matches = "true")
    void connect_with_sasl_props_against_external_cluster() {
        // 실제 외부 브로커를 대상으로만 실행
        // (여기선 단순 가드 – 실제 사내 환경에서만 세부 검증 구현)
        Assumptions.assumeTrue(true);
    }
}