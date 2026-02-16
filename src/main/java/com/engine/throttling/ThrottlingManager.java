package com.engine.throttling;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import java.time.Duration;

public class ThrottlingManager {
    private final RateLimiterRegistry registry;

    public ThrottlingManager() {
        // Default configuration for the rate limiter
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(50) // Default to 50 req/sec as per REST requirement [cite: 27]
                .timeoutDuration(Duration.ofSeconds(5))
                .build();

        this.registry = RateLimiterRegistry.of(config);
    }

    public RateLimiter getLimiter(String name, int limit) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(limit)
                .timeoutDuration(Duration.ofSeconds(5))
                .build();
        return registry.rateLimiter(name, config);
    }
}