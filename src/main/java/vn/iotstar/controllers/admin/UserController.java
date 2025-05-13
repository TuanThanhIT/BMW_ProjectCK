package vn.iotstar.controllers.admin;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import vn.iotstar.entity.User;
import vn.iotstar.services.IUserService;

@Controller
@RequestMapping("admin/user")
public class UserController {
	@Autowired
	IUserService iUserService;

//	@RequestMapping("")
//	public String listCategories(ModelMap model) {
//		List<User> list = iUserService.findAll();
//		System.out.println("Danh sách users: " + list); // Debug kiểm tra
//		model.addAttribute("users", list); // Tên biến là "milkTeaTypes"
//		return "admin/user/list"; // Tên View
//	}
	@RequestMapping("")
	public String listCategories(@RequestParam(defaultValue = "0") int page, ModelMap model,
			HttpServletRequest request) {

			// Sử dụng pageable để giới hạn số sản phẩm hiển thị trên mỗi trang
			Pageable pageable = PageRequest.of(page, 5); // 5 sản phẩm mỗi trang
			Page<User> milkTeaTypesPage = iUserService.findAll(pageable);

			// Thêm các thuộc tính vào model để truyền cho view
			model.addAttribute("users", milkTeaTypesPage.getContent()); // Dữ liệu sản phẩm
			model.addAttribute("currentPage", page); // Trang hiện tại
			model.addAttribute("totalPages", milkTeaTypesPage.getTotalPages()); // Tổng số trang
			return "admin/user/list"; // View name

	}

//	@RequestMapping("toggleActive/{userID}")
//	@ResponseBody // Để trả về dữ liệu trực tiếp (thường cho AJAX)
//	public ResponseEntity<?> toggleUserActive(@PathVariable Integer userID) {
//	    try {
//	        Optional<User> userOpt = iUserService.findById(userID); // Tìm user
//	        if (userOpt.isPresent()) {
//	            User user = userOpt.get();
//	            user.setActive(!user.isActive()); // Đổi trạng thái
//	            iUserService.save(user); // Lưu vào DB
//	            return ResponseEntity.ok("Trạng thái tài khoản đã được thay đổi!");
//	        } else {
//	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//	                    .body("Không tìm thấy người dùng có ID: " + userID);
//	        }
//	    } catch (Exception e) {
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body("Có lỗi xảy ra: " + e.getMessage());
//	    }
//	}
	@RequestMapping("toggleActive/{userID}")
	public String toggleUserActive(@PathVariable Integer userID, ModelMap model) {
		try {
			Optional<User> userOpt = iUserService.findById(userID); // Tìm user
			if (userOpt.isPresent()) {
				User user = userOpt.get();
				user.setActive(!user.isActive()); // Đổi trạng thái
				iUserService.save(user); // Lưu vào DB

				// Thêm thông báo vào ModelMap và quay lại trang danh sách
				model.addAttribute("message", "Trạng thái tài khoản đã được thay đổi!");
				return "forward:/admin/user"; // Quay lại trang danh sách users
			} else {
				model.addAttribute("message", "Không tìm thấy người dùng có ID: " + userID);
				return "redirect:/admin/user"; // Quay lại trang danh sách nếu không tìm thấy người dùng
			}
		} catch (Exception e) {
			model.addAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
			return "redirect:/admin/user"; // Quay lại trang danh sách nếu có lỗi
		}
	}

}
