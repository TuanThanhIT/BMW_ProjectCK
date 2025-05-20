package vn.iotstar.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.model.UserDto;
import vn.iotstar.services.*;
import vn.iotstar.utils.PathConstants;

@Controller
@RequestMapping("/register")
public class RegisterController {

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IOtpService otpService; // Inject OtpService

	@Autowired
	private IEmailService emailService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private IRateLimiterService rateLimiterService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private IRecaptchaService recaptchaService;

	@Value("${recaptcha.key}")
	private String recaptchaSiteKey;

	// Hiển thị trang đăng ký
	@GetMapping
	public String showRegisterPage(Model model) {
		// Lấy danh sách các role không phải "Admin"
		List<Role> list = roleService.findByRoleNameNot("Admin");
		model.addAttribute("listRole", list);
		UserDto userDto = new UserDto();
		model.addAttribute("userDto", userDto);
		model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
		return "register";
	}

	@PostMapping
	public String createUser(@Valid @ModelAttribute UserDto userDto,
							 @RequestParam("g-recaptcha-response") String captchaResponse,
							 Model model) {

		String clientIp = request.getRemoteAddr();

		// 1) Kiểm tra reCAPTCHA
		if (!recaptchaService.verify(captchaResponse, clientIp)) {
			model.addAttribute("recaptchaError", "Vui lòng xác thực reCAPTCHA");
			model.addAttribute("userDto", userDto);
			model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
			List<Role> list = roleService.findByRoleNameNot("Admin");
			model.addAttribute("listRole", list);
			return "register";
		}
		
		String message2 = "";
		if (!isPasswordStrong(userDto.getPassword())) {
		    message2 = "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.";
		    model.addAttribute("alert", message2);
		    return "register";
		}
		
		String ip = rateLimiterService.getClientIP(request);
		Bucket bucket = rateLimiterService.resolveBucket(ip);

		if (!bucket.tryConsume(1)) {
		    model.addAttribute("alert", "Bạn đã thực hiện quá nhiều yêu cầu. Vui lòng thử lại sau.");
		    return "register";
		}
		
		String message = "";

		String uploadDirectory = PathConstants.UPLOAD_DIRECTORY;

		Date createdAt = new Date();
		MultipartFile image = userDto.getImage();
		String originalFileName = image.getOriginalFilename();
		String storageFileName = createdAt.getTime() + "_" + originalFileName;

		try {
			// Kiểm tra định dạng file (chỉ cho phép jpg, png, jpeg)
			if (!originalFileName.toLowerCase().endsWith(".jpg") && !originalFileName.toLowerCase().endsWith(".png")
					&& !originalFileName.toLowerCase().endsWith(".jpeg")) {
				message = "Đăng kí không thành công, chỉ chấp nhận các định dạng ảnh: JPG, PNG, JPEG.";
				model.addAttribute("alert", message);
				return "register";
			}
			// Thư mục lưu trữ ảnh
			Path uploadPath = Paths.get(uploadDirectory); // Chỉ đường dẫn đến thư mục uploads

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath); // Nếu thư mục không tồn tại, tạo thư mục
			}

			try (InputStream inputStream = image.getInputStream()) {
				// Đảm bảo lưu vào đúng thư mục uploads với tên file lưu trữ
				Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception ex) {
			System.out.println("Exception:" + ex.getMessage());
		}
		// Kiểm tra xem tên đăng nhập có tồn tại hay không
		if (userService.existsByUserName(userDto.getUserName())) {
			message = "Tên đăng nhập đã tồn tại. Đăng kí không thành công";
			model.addAttribute("alert", message);
			return "register";
		}

		// Lưu thông tin người dùng vào cơ sở dữ liệu tạm thời (chưa lưu password)
		User user = new User();
		user.setFullName(userDto.getFullName());
		user.setAddress(userDto.getAddress());
		user.setPhone(userDto.getPhone());
		user.setEmail(userDto.getEmail());
		user.setUserName(userDto.getUserName());
		String encodedPassword = passwordEncoder.encode(userDto.getPassword());
		user.setPassword(encodedPassword);
		user.setImage(storageFileName);
		user.setDate(new Date());
		user.setActive(false); // Người dùng chưa kích hoạt tài khoản
		user.setRoleID(userDto.getRoleID());

		if (userService.existsByEmail(user.getEmail())) {
			message = "Email đã tồn tại, vui lòng đăng kí email khác";
			model.addAttribute("alert", message);
			return "register";
		} else {
			userService.addUser(user);

			// Tạo và gửi mã OTP qua email
			String otp = otpService.generateOtp(user.getEmail());
			String subject = "Mã OTP kích hoạt tài khoản";
			String text = "Mã OTP của bạn là: " + otp + ". Mã OTP sẽ hết hạn sau 5 phút.";
			emailService.sendEmail(user.getEmail(), subject, text);

			// Chuyển hướng đến trang nhập OTP
			model.addAttribute("email", user.getEmail()); // Lưu email vào model để người dùng không phải nhập lại
			return "verify-otp"; // Chuyển đến trang nhập OTP
		}

	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam String otp, @RequestParam String email, Model model) {
		boolean isValidOtp = otpService.validateOtp(email, otp);

		if (!isValidOtp) {
			model.addAttribute("alert", "Mã OTP không chính xác hoặc đã hết hạn.");
			// Giữ lại email trong model để người dùng không phải nhập lại
			model.addAttribute("email", email);
			return "verify-otp"; // Quay lại trang nhập OTP
		}

		// Sau khi OTP hợp lệ, cập nhật trạng thái người dùng thành "active"
		User user = userService.findByEmail(email);
		if (user != null) {
			user.setActive(true); // Kích hoạt tài khoản người dùng
			userService.save(user);
		}

		model.addAttribute("successMessage", "Đăng ký thành công! Hãy đăng nhập.");
		return "login";
	}
	
	public boolean isPasswordStrong(String password) {
	    // Kiểm tra mật khẩu mạnh
	    String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/]).{8,}$";
	    return password.matches(passwordPattern);
	}

}
