package vn.iotstar.services.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.RefreshToken;
import vn.iotstar.repository.RefreshTokenRepository;
import vn.iotstar.services.IRefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Value("${jwt.refreshExpiration}")
    private long refreshTokenExpiration;

    @Override
    public RefreshToken createRefreshToken(String username, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + refreshTokenExpiration)); // 7 ngày
        refreshToken.setRevoked(false);
        refreshToken.setCreatedAt(new Date());
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public Optional<RefreshToken> findByUsername(String username) {
        return refreshTokenRepository.findByUsername(username);
    }

    @Override
    public boolean validateRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);
        if (refreshTokenOpt.isPresent()) {
            RefreshToken refreshToken = refreshTokenOpt.get();
            return !refreshToken.isRevoked() && refreshToken.getExpiryDate().after(new Date());
        }
        return false;
    }

    @Override
    public void revokeRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);
        if (refreshTokenOpt.isPresent()) {
            RefreshToken refreshToken = refreshTokenOpt.get();
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        }
    }

    @Override
    public void deleteByUsername(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }
}
