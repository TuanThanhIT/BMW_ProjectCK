package vn.iotstar.services;

public interface ILoginAttemptService {

	int getFailedAttempts(String key);

	boolean isBlocked(String key);

	void loginSucceeded(String key);

	void loginFailed(String key);

}
