package vn.iotstar.configs;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

    // Sử dụng Set cho whitelist hiệu quả hơn
    private static final Set<String> WHITELIST_PATHS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/api/public"
    )));

    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

    @Value("${rate.limit.capacity:1000}")
    private int capacity;

    @Value("${rate.limit.duration.minutes:5}")
    private int durationMinutes;

    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();

    public RateLimitFilter() {
        cleanupExecutor.scheduleAtFixedRate(this::cleanupBuckets, 5, 5, TimeUnit.MINUTES);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String path = request.getRequestURI();

        if (isWhitelisted(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String ip = getClientIP(request);
        final Bucket bucket = ipBuckets.computeIfAbsent(ip, this::createBucket);
        final ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            addRateLimitHeaders(response, probe);
            filterChain.doFilter(request, response);
        } else {
            handleRateLimitExceeded(response, probe, ip, path);
        }
    }

    private boolean isWhitelisted(String path) {
        return WHITELIST_PATHS.stream().anyMatch(path::startsWith);
    }

    private Bucket createBucket(String ip) {
        return Bucket.builder()
                .addLimit(Bandwidth.simple(capacity, Duration.ofMinutes(durationMinutes)))
                .build();
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
<<<<<<< HEAD
    }*/
=======
    }

    private void addRateLimitHeaders(HttpServletResponse response, ConsumptionProbe probe) {
        response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
    }

    private void handleRateLimitExceeded(HttpServletResponse response, ConsumptionProbe probe, String ip, String path)
            throws IOException {
        if (logger.isWarnEnabled()) {
            logger.warn("Rate limit exceeded - IP: {}, Path: {}, Retry After: {}s",
                    ip, path, probe.getNanosToWaitForRefill() / 1_000_000_000);
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader("Retry-After", String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000));
        response.getWriter().write("Too many requests. Please try again later.");
    }

    private void cleanupBuckets() {
        final int initialSize = ipBuckets.size();
        ipBuckets.entrySet().removeIf(entry ->
                entry.getValue().getAvailableTokens() >= capacity
        );

        if (logger.isDebugEnabled() && ipBuckets.size() != initialSize) {
            logger.debug("Bucket cleanup completed. Removed {} entries", initialSize - ipBuckets.size());
        }
    }

    @Override
    public void destroy() {
        cleanupExecutor.shutdownNow();
        ipBuckets.clear();
        super.destroy();
    }
}
>>>>>>> 91b64db7a67ba3e79e60667632b5e8160dae6c44
