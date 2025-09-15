package com.project.sleep.global.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties("jwt-properties")
public class JwtProperties {

    private final String key;
    private final Long accessTokenExpirationPeriodDay;
    private final Long refreshTokenExpirationPeriodDay;
    private final String accessTokenSubject;
    private final String refreshTokenSubject;
    private final String tokenHeader;
    private final String bearer;
    private final String id;

}
