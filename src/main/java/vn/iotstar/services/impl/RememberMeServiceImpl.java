package vn.iotstar.services.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.RememberMeToken;
import vn.iotstar.repository.RememberMeTokenRepository;
import vn.iotstar.services.IRememberMeService;

@Service
public class RememberMeServiceImpl implements IRememberMeService{

    @Autowired
    private RememberMeTokenRepository rememberMeTokenRepository;

    @Override
	public void saveToken(String username, String token, LocalDateTime expiryDate) {
        RememberMeToken rememberMeToken = new RememberMeToken();
        rememberMeToken.setUsername(username);
        rememberMeToken.setToken(token);
        rememberMeToken.setExpiryDate(expiryDate);

        rememberMeTokenRepository.save(rememberMeToken); // Lưu vào DB
    }
    
    @Override
	public String getUsernameByToken(String token) {
        // Truy vấn token trong DB để lấy thông tin username
        RememberMeToken rememberMeToken = rememberMeTokenRepository.findByToken(token);
        if (rememberMeToken != null) {
            return rememberMeToken.getUsername(); // Trả về username từ token
        }
        return null; // Nếu không tìm thấy token hợp lệ, trả về null
    }
}