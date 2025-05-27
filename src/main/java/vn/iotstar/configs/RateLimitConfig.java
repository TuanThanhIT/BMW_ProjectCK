package vn.iotstar.configs;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitConfig.class);
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Bean
    public Map<String, Bucket> buckets() {
        return buckets;
    }

    public Bucket resolveBucket(String ipAddress) {
        return buckets.computeIfAbsent(ipAddress, this::createNewBucket);
    }

    private Bucket createNewBucket(String ipAddress) {
        // Giới hạn 50 request trong 1 phút cho mỗi IP
        Bandwidth limit = Bandwidth.simple(50, Duration.ofMinutes(1));
        logger.info("Created new rate limit bucket for IP: {}", ipAddress);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
} 