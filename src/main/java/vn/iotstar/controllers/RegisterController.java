package vn.iotstar.controllers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.model.UserDto;
import vn.iotstar.services.*;
import vn.iotstar.utils.PathConstants;

import javax.imageio.ImageIO;

@Controller
@RequestMapping("/register")
public class RegisterController {

	@Autowired private IRoleService roleService;

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
	public String showRegisterForm(Model model) {
		List<Role> roles = roleService.findByRoleNameNot("Admin");
		model.addAttribute("listRole", roles);
		model.addAttribute("userDto", new UserDto());
		model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
		return "register";
	}

	// Xử lý đăng ký
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
			message2 = "Mật khẩu cần ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt.";
			model.addAttribute("alert", message2);
			return "register";
		}

		// Bước 2: Giới hạn tần suất gửi yêu cầu
		String ip = rateLimiterService.getClientIP(request);
		Bucket bucket = rateLimiterService.resolveBucket(ip);
		if (!bucket.tryConsume(1)) {
			model.addAttribute("alert", "Bạn đã gửi quá nhiều yêu cầu. Vui lòng thử lại sau.");
			return "register";
		}

		// Bước 3: Kiểm tra file ảnh
		MultipartFile imageFile = userDto.getImage();
		if (imageFile == null || imageFile.isEmpty()) {
			model.addAttribute("alert", "Vui lòng chọn ảnh đại diện.");
			return "register";
		}

		String originalFilename = imageFile.getOriginalFilename();
		if (originalFilename == null) {
			model.addAttribute("alert", "Tên file không hợp lệ.");
			return "register";
		}

		String fileExtension = originalFilename.toLowerCase();
		if (!fileExtension.endsWith(".jpg") && !fileExtension.endsWith(".jpeg") && !fileExtension.endsWith(".png")) {
			model.addAttribute("alert", "Chỉ hỗ trợ ảnh định dạng JPG, JPEG, PNG.");
			return "register";
		}

		// Bước 4: Lưu file tạm và kiểm tra MIME + ảnh thật
		String message = "";
		String fileName = System.currentTimeMillis() + "_" + originalFilename;
		Path uploadPath = Paths.get(PathConstants.UPLOAD_DIRECTORY);

		try {
			if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
			Path filePath = uploadPath.resolve(fileName);

			// Lưu tệp
			try (InputStream inputStream = imageFile.getInputStream()) {
				Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			}

			// Kiểm tra MIME
			String mimeType = Files.probeContentType(filePath);
			List<String> allowedMimeTypes = List.of("image/jpeg", "image/png");
			if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
				Files.deleteIfExists(filePath);
				model.addAttribute("alert", "Tệp tải lên không phải ảnh hợp lệ.");
				return "register";
			}

			// Kiểm tra ảnh thật sự
			BufferedImage bufferedImage = ImageIO.read(filePath.toFile());
			if (bufferedImage == null) {
				Files.deleteIfExists(filePath);
				model.addAttribute("alert", "Tệp tải lên không phải là ảnh.");
				return "register";
			}

		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("alert", "Đã xảy ra lỗi khi xử lý ảnh đại diện.");
			return "register";
		}

		// Bước 5: Kiểm tra username và email
		if (userService.existsByUserName(userDto.getUserName())) {
			message = "Tên đăng nhập đã tồn tại. Đăng kí không thành công";
			model.addAttribute("recaptchaError", "Vui lòng xác thực reCAPTCHA");
			model.addAttribute("userDto", userDto);
			model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
			List<Role> list = roleService.findByRoleNameNot("Admin");
			model.addAttribute("listRole", list);
			model.addAttribute("alert", message);
			return "register";
		}

		if (userService.existsByEmail(userDto.getEmail())) {
			model.addAttribute("alert", "Email đã tồn tại.");
			model.addAttribute("recaptchaError", "Vui lòng xác thực reCAPTCHA");
			model.addAttribute("userDto", userDto);
			model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
			List<Role> list = roleService.findByRoleNameNot("Admin");
			model.addAttribute("listRole", list);
			return "register";
		}

		// Bước 6: Tạo người dùng
		User user = new User();
		user.setFullName(userDto.getFullName());
		user.setAddress(userDto.getAddress());
		user.setPhone(userDto.getPhone());
		user.setEmail(userDto.getEmail());
		user.setUserName(userDto.getUserName());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		user.setImage(fileName);
		user.setDate(new Date());
		user.setActive(false);
		user.setRoleID(userDto.getRoleID());

		if (userService.existsByEmail(user.getEmail())) {
			if(userService.checkEmailPattern(user.getEmail())) {
				message = "Email không hợp lệ";
			}
			else{
				message = "Email đã tồn tại, vui lòng đăng kí email khác";
			}
			model.addAttribute("recaptchaError", "Vui lòng xác thực reCAPTCHA");
			model.addAttribute("userDto", userDto);
			model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
			List<Role> list = roleService.findByRoleNameNot("Admin");
			model.addAttribute("listRole", list);
			model.addAttribute("alert", message);
			return "register";
		} else {
			userService.addUser(user);

		// Bước 7: Gửi OTP
		String otp = otpService.generateOtp(user.getEmail());
		String subject = "Mã OTP kích hoạt tài khoản";
		String body = "Mã OTP của bạn là: " + otp + ". Mã có hiệu lực trong 5 phút.";
		emailService.sendEmail(user.getEmail(), subject, body);

			// Chuyển hướng đến trang nhập OTP
			HttpSession session = request.getSession();
			session.setAttribute("otpAttempts", 0);     // khởi tạo đếm lần sai OTP
			model.addAttribute("email", user.getEmail()); // Lưu email vào model để người dùng không phải nhập lại
			return "verify-otp"; // Chuyển đến trang nhập OTP
		}

	}

	// Xác minh OTP
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam String otp,
							@RequestParam String email,
							@RequestParam(name="g-recaptcha-response", required = false) String captchaResponse,
							Model model) {
		HttpSession session = request.getSession();
		Integer attempts = (Integer) session.getAttribute("otpAttempts");
		if (attempts == null)
			attempts = 0;

		// 1) Nếu đã sai >=3 lần, bắt require reCAPTCHA
		if (attempts >= 3) {
			model.addAttribute("showRecaptcha", true);
			model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);

			if (captchaResponse == null || !recaptchaService.verify(captchaResponse, request.getRemoteAddr())) {
				model.addAttribute("recaptchaError", "Vui lòng xác thực reCAPTCHA");
				model.addAttribute("email", email);
				return "verify-otp";
			}
		}

		boolean isValidOtp = otpService.validateOtp(email, otp);
		if (!isValidOtp) {
			// Tăng biến đếm nếu nhập sai và thêm vào session
			attempts++;
			session.setAttribute("otpAttempts", attempts);
			model.addAttribute("alert", "Mã OTP không chính xác hoặc đã hết hạn.");
			model.addAttribute("email", email);

			// Nếu mới đạt 3 lần, bật recaptcha
			if (attempts >= 3) {
				model.addAttribute("showRecaptcha", true);
				model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
			}

			return "verify-otp"; // Quay lại trang nhập OTP
		}

		// Sau khi OTP hợp lệ, cập nhật trạng thái người dùng thành "active". Xóa counter và active user
		session.removeAttribute("otpAttempts");
		User user = userService.findByEmail(email);
		if (user != null) {
			user.setActive(true);
			userService.save(user);
		}

		model.addAttribute("successMessage", "Đăng ký thành công! Mời đăng nhập.");
		return "login";
	}

	// Hàm kiểm tra mật khẩu mạnh
	private boolean isPasswordStrong(String password) {
		String pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/]).{8,}$";
		return password.matches(pattern);
	}
}
