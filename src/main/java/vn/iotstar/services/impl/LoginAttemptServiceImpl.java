package vn.iotstar.services.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import vn.iotstar.services.ILoginAttemptService;

@Service
public class LoginAttemptServiceImpl implements ILoginAttemptService{
	private final int MAX_ATTEMPT = 5;
    private final int BLOCK_TIME_MINUTES = 5;

    private Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private Map<String, LocalDateTime> blocked = new ConcurrentHashMap<>();

    @Override
	public void loginFailed(String key) {
        int attemptsCount = attempts.getOrDefault(key, 0);
        attempts.put(key, attemptsCount + 1);
        if (attemptsCount + 1 >= MAX_ATTEMPT) {
            blocked.put(key, LocalDateTime.now().plusMinutes(BLOCK_TIME_MINUTES));
        }
    }

    @Override
	public void loginSucceeded(String key) {
        attempts.remove(key);
        blocked.remove(key);
    }

    @Override
	public boolean isBlocked(String key) {
        if (!blocked.containsKey(key)) return false;
        if (LocalDateTime.now().isAfter(blocked.get(key))) {
            // Hết thời gian block
            blocked.remove(key);
            attempts.remove(key);
            return false;
        }
        return true;
    }

    @Override
	public int getFailedAttempts(String key) {
        return attempts.getOrDefault(key, 0);
    }

}
