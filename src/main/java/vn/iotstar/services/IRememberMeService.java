package vn.iotstar.services;

import java.time.LocalDateTime;

public interface IRememberMeService {

	void saveToken(String username, String token, LocalDateTime expiryDate);

	String getUsernameByToken(String token);

}
