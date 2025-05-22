package vn.iotstar.controllers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired private IUserService userService;
	@Autowired private IOtpService otpService;
	@Autowired private IEmailService emailService;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private IRateLimiterService rateLimiterService;
	@Autowired private HttpServletRequest request;

	// Hiển thị form đăng ký
	@GetMapping
	public String showRegisterForm(Model model) {
		List<Role> roles = roleService.findByRoleNameNot("Admin");
		model.addAttribute("listRole", roles);
		model.addAttribute("userDto", new UserDto());
		return "register";
	}

	// Xử lý đăng ký
	@PostMapping
	public String registerUser(@Valid @ModelAttribute UserDto userDto, Model model) {
		String alert;

		// Bước 1: Kiểm tra mật khẩu mạnh
		if (!isPasswordStrong(userDto.getPassword())) {
			alert = "Mật khẩu cần ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt.";
			model.addAttribute("alert", alert);
			return "register";
		}

		// Bước 2: Giới hạn tần suất gửi yêu cầu
		String clientIp = rateLimiterService.getClientIP(request);
		Bucket bucket = rateLimiterService.resolveBucket(clientIp);
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
			model.addAttribute("alert", "Tên đăng nhập đã tồn tại.");
			return "register";
		}

		if (userService.existsByEmail(userDto.getEmail())) {
			model.addAttribute("alert", "Email đã tồn tại.");
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

		userService.addUser(user);

		// Bước 7: Gửi OTP
		String otp = otpService.generateOtp(user.getEmail());
		String subject = "Mã OTP kích hoạt tài khoản";
		String body = "Mã OTP của bạn là: " + otp + ". Mã có hiệu lực trong 5 phút.";
		emailService.sendEmail(user.getEmail(), subject, body);

		model.addAttribute("email", user.getEmail());
		return "verify-otp";
	}

	// Xác minh OTP
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam String otp, @RequestParam String email, Model model) {
		if (!otpService.validateOtp(email, otp)) {
			model.addAttribute("alert", "Mã OTP không chính xác hoặc đã hết hạn.");
			model.addAttribute("email", email);
			return "verify-otp";
		}

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
