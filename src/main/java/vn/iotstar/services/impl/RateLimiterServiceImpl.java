package vn.iotstar.services.impl;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import vn.iotstar.services.IRateLimiterService;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterServiceImpl implements IRateLimiterService{

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
	public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> {
            Refill refill = Refill.greedy(5, Duration.ofMinutes(1)); // 5 lần/phút
            Bandwidth limit = Bandwidth.classic(5, refill);
            return Bucket.builder().addLimit(limit).build();
        });
    }

    @Override
	public String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return xfHeader != null ? xfHeader.split(",")[0] : request.getRemoteAddr();
    }
}
