package vn.iotstar.services;

import java.util.Optional;

import vn.iotstar.entity.RefreshToken;

public interface IRefreshTokenService {
	RefreshToken createRefreshToken(String username, String token, String ip, String userAgent);
	Optional<RefreshToken> findByToken(String token);
	Optional<RefreshToken> findByUsername(String username);
	boolean validateRefreshToken(String token, String ip, String userAgent);
	void revokeRefreshToken(String token);
	void deleteByUsername(String username);
}
