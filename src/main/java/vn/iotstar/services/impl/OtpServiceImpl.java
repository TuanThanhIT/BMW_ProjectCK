package vn.iotstar.services.impl;

import org.springframework.stereotype.Service;
import vn.iotstar.services.IOtpService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpServiceImpl implements IOtpService {
    private final Map<String, String> otpStore = new HashMap<>();
    private final Map<String, Long> otpExpiry = new HashMap<>();
    private final int OTP_VALID_DURATION = 5 * 60 * 1000; // 5 phút

    // Tạo mã OTP
    @Override
    public String generateOtp(String email) {
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(999999));
        otpStore.put(email, otp);
        otpExpiry.put(email, System.currentTimeMillis() + OTP_VALID_DURATION);  // Lưu thời gian hết hạn
        return otp;
    }

    // Kiểm tra mã OTP
    @Override
    public boolean validateOtp(String email, String otp) {
        if (!otpStore.containsKey(email)) {
            return false;  // Không tìm thấy OTP cho email này
        }

        // Kiểm tra nếu OTP đã hết hạn
        if (otpExpiry.get(email) < System.currentTimeMillis()) {
            otpStore.remove(email);  // Xóa OTP nếu hết hạn
            otpExpiry.remove(email);
            return false;  // OTP hết hạn
        }

        // Kiểm tra sự hợp lệ của OTP
        boolean isValid = otpStore.get(email).equals(otp);
        if (isValid) {
            otpStore.remove(email);  // Xóa OTP nếu hợp lệ
            otpExpiry.remove(email);
        }

        return isValid;  // Trả về true nếu OTP hợp lệ
    }

    // Kiểm tra thời gian hết hạn OTP
    public Long getOtpExpiryTime(String email) {
        return otpExpiry.get(email);
    }

    // Kiểm tra xem OTP có còn hợp lệ hay không
    public boolean isOtpValid(String email) {
        return otpExpiry.containsKey(email) && otpExpiry.get(email) > System.currentTimeMillis();
    }
}
