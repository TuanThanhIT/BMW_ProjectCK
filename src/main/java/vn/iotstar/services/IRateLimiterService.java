package vn.iotstar.services;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;

public interface IRateLimiterService {

	String getClientIP(HttpServletRequest request);

	Bucket resolveBucket(String key);

}
