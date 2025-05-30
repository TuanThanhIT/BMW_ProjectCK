package vn.iotstar.controllers;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.configs.JwtTokenProvider;
import vn.iotstar.entity.User;
import vn.iotstar.services.IEmailService;
import vn.iotstar.services.ILoginAttemptService;
import vn.iotstar.services.IOtpService;
import vn.iotstar.services.IRefreshTokenService;
import vn.iotstar.services.IRememberMeService;
import vn.iotstar.services.IUserService;

@Controller
@RequestMapping("/login")
public class LoginController {
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IEmailService emailService;
	
	@Autowired
	private IOtpService otpService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private IRememberMeService rememberMeService;
	
	@Autowired
	private ILoginAttemptService loginAttemptService;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private IRefreshTokenService refreshTokenService;

	@Value("${jwt.expiration}")
    private long accessTokenExpiration;
    
    @Value("${jwt.refreshExpiration}")
    private long refreshTokenExpiration;
	
	@GetMapping
	public String showLoginPage(HttpServletRequest request, HttpServletResponse response, Model model) {
		
	    Cookie[] cookies = request.getCookies();
		
	    // Kiểm tra cookie nhớ mật khẩu
	    String username = "";
	    boolean rememberMe = false;

	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("REMEMBER_ME_TOKEN".equals(cookie.getName())) {
	                // Nếu có cookie nhớ mật khẩu, chỉ lấy username, không lấy password
	                String token = cookie.getValue();
	                String storedUsername = rememberMeService.getUsernameByToken(token);  // Giả sử có phương thức này để lấy username từ token
	                if (storedUsername != null) {
	                    username = storedUsername;
	                    rememberMe = true; // Đánh dấu là đã chọn nhớ mật khẩu
	                }
	            }
	        }
	    }

	    // Truyền vào model để hiển thị thông tin trên form
	    model.addAttribute("username", username);
	    model.addAttribute("rememberMe", rememberMe); // Checkbox nhớ mật khẩu được đánh dấu nếu cookie tồn tại

	    return "login";
	}


	@PostMapping
	public String handleLogin(@RequestParam("username") String username, @RequestParam("password") String password,
	                          @RequestParam(value = "remember", required = false) String remember, HttpServletRequest request,
	                          HttpServletResponse response, Model model) {
		
		String clientIP = request.getRemoteAddr(); // có thể thay bằng username nếu muốn giới hạn theo tài khoản

		if (loginAttemptService.isBlocked(clientIP)) {
		    model.addAttribute("alert", "Tài khoản hoặc IP của bạn đã bị khóa tạm thời do đăng nhập sai quá nhiều. Vui lòng thử lại sau.");
		    return "login";
		}
		
		boolean isRememberMe = "on".equals(remember); // Kiểm tra xem người dùng có chọn nhớ mật khẩu không
	    String alertMsg = "";

	    // Kiểm tra thông tin đầu vào
	    if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
	        alertMsg = "Tài khoản hoặc mật khẩu không được rỗng";
	        model.addAttribute("alert", alertMsg);
	        return "login";
	    }

	    // Kiểm tra thông tin đăng nhập
	    User user = userService.loadUserByUsername(username);
	    if (user != null && passwordEncoder.matches(password, user.getPassword())) {
	        if (user.isActive()) { // Kiểm tra tài khoản có bị khóa không
	        	loginAttemptService.loginSucceeded(clientIP); // reset lại nếu đúng
	            
	            // Tạo Access Token
                String accessToken = jwtTokenProvider.generateAccessToken(username);

				String jwtCookieValue = "JWT_TOKEN=" + accessToken +
						"; Path=/; HttpOnly; Secure; Max-Age=" + (accessTokenExpiration / 1000) +
						"; SameSite=Strict";
				response.addHeader("Set-Cookie", jwtCookieValue);
	            
                // Tạo và lưu Refresh Token
				String ip = request.getRemoteAddr();
				String userAgent = request.getHeader("User-Agent");
                String refreshToken = jwtTokenProvider.generateRefreshToken(username);
                refreshTokenService.createRefreshToken(username, refreshToken, ip , userAgent);

				String refreshCookieValue = "REFRESH_TOKEN=" + refreshToken +
						"; Path=/; HttpOnly; Secure; Max-Age=" + (refreshTokenExpiration / 1000) +
						"; SameSite=Strict";
				response.addHeader("Set-Cookie", refreshCookieValue);

	            // Nếu chọn nhớ mật khẩu thì lưu cookie
	            if (isRememberMe) {
	                saveRememberMe(response, username);
	            } else {
	                deleteRememberMeCookie(response); // Nếu không chọn thì xóa cookie
	            }

	            return "redirect:/user/home"; // Chuyển hướng sau khi đăng nhập thành công
	        } else {
	            alertMsg = "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên để xử lý.";
	            model.addAttribute("alert", alertMsg);
	            return "login";
	        }
	    } else {
	    	loginAttemptService.loginFailed(clientIP); // tăng số lần sai
	        model.addAttribute("alert", "Tài khoản hoặc mật khẩu không đúng (" + 
	                            loginAttemptService.getFailedAttempts(clientIP) + " lần)");
	        return "login";
	    }
	}

	// Lưu cookie nhớ mật khẩu
	private void saveRememberMe(HttpServletResponse response, String username) {
	    String token = UUID.randomUUID().toString(); // Sinh token ngẫu nhiên
	    LocalDateTime expiryDate = LocalDateTime.now().plusDays(7); // Hết hạn sau 7 ngày

	    rememberMeService.saveToken(username, token, expiryDate); // Lưu vào DB

		String cookieValue = "REMEMBER_ME_TOKEN=" + token +
				"; Path=/; HttpOnly; Secure; Max-Age=" + (7 * 24 * 60 * 60) +
				"; SameSite=Strict";

		response.addHeader("Set-Cookie", cookieValue);
	}

	// Xóa cookie khi không chọn "Nhớ mật khẩu"
	private void deleteRememberMeCookie(HttpServletResponse response) {

		String cookieValue = "REMEMBER_ME_TOKEN=; Path=/; HttpOnly; Secure; Max-Age=0; SameSite=Strict";
		response.addHeader("Set-Cookie", cookieValue);
	}


	@GetMapping("forgot-password")
	public String showForgotPasswordPage() {
		return "forgot-password";

	}
	
	@PostMapping("forgot-password")
	public String sendOtpToEmail(@RequestParam("email") String email, Model model) 
	{
		if (!userService.existsByEmail(email)) {
            model.addAttribute("alert", "Email không tồn tại trong hệ thống!");
            return "forgot-password";  // Quay lại trang nhập email nếu không tìm thấy email
        }

        // Tạo mã OTP và gửi qua email
        String otp = otpService.generateOtp(email);
        String subject = "Mã OTP xác nhận thay đổi mật khẩu";
        String text = "Mã OTP của bạn là: " + otp + ". Mã OTP sẽ hết hạn sau 5 phút.";
        emailService.sendEmail(email, subject, text);

        model.addAttribute("email", email);  // Gửi lại email để điền vào ô khi xác thực OTP
        return "verify-otp-pass";  // Chuyển sang trang nhập OTP
	}
	
	@PostMapping("verify-otp-pass")
	 public String verifyOtp(@RequestParam String otp, @RequestParam String newPassword, @RequestParam String email, Model model) {
        boolean isValidOtp = otpService.validateOtp(email, otp);

        if (!isValidOtp) {
            model.addAttribute("alert", "Mã OTP không hợp lệ hoặc đã hết hạn.");
            model.addAttribute("email", email);
            return "verify-otp-pass";  // Quay lại trang nhập OTP
        }
        
        String message2 = "";
		if (!isPasswordStrong(newPassword)) {
			message2 = "Mật khẩu cần ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt.";
			model.addAttribute("alert", message2);
			return "verify-otp-pass";
		}

        // Cập nhật mật khẩu mới
        User user = userService.findByEmail(email);
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userService.save(user);

        model.addAttribute("successMessage", "Mật khẩu đã được thay đổi thành công!");
        return "login";  // Chuyển đến trang đăng nhập
    }
	

	// Hàm kiểm tra mật khẩu mạnh
	private boolean isPasswordStrong(String password) {
		String pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/]).{8,}$";
		return password.matches(pattern);
	}
}