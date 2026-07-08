package com.hh.oneplusplus.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Value("${app.cache.template.max-size}")
    private long maxSize;

    @Value("${app.cache.template.expire-hours}")
    private long expireHours;
    @Bean
    public Cache<String, String> templateCache(){
        return Caffeine.newBuilder().
                maximumSize(maxSize)
                .expireAfterAccess(Duration.ofHours(expireHours))
                .build();
    }
}
