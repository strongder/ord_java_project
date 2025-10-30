package com.ord.tutorial.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private final Jwt jwt = new Jwt();

    @Data
    public static class Jwt {
        private String secret;
        private long expiration = 86_400_000L;
        /**
         * Thời gian hết hạn refresh token (milliseconds)
         * 604800000 = 7 ngày
         */
        private long refreshExpiration = 604_800_000L;
    }
}
