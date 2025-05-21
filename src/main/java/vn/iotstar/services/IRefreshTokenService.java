package vn.iotstar.services;

import java.util.Optional;

import vn.iotstar.entity.RefreshToken;

public interface IRefreshTokenService {
	RefreshToken createRefreshToken(String username, String token);
	Optional<RefreshToken> findByToken(String token);
	Optional<RefreshToken> findByUsername(String username);
	boolean validateRefreshToken(String token);
	void revokeRefreshToken(String token);
	void deleteByUsername(String username);
}
