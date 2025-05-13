package vn.iotstar.controllers.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.User;
import vn.iotstar.services.IOrderService;

@Controller
@RequestMapping("user/packages")
public class PackageController {
	
	@Autowired
	private IOrderService orderServ;

	@GetMapping({ "", "/" })
	public String getOrder(HttpSession session, Model model)
	{
		  // Lấy thông tin người dùng từ session
	    User user = (User) session.getAttribute("account");
	    if (user == null) {
	        return "redirect:/login"; // Chuyển hướng về trang đăng nhập nếu chưa đăng nhập
	    }

	    // Tìm các đơn hàng theo User ID
	    List<Order> orders = orderServ.findByUserId(user.getUserID());

	    // Đưa danh sách đơn hàng vào model
	    model.addAttribute("orders", orders);
		
		return "user/packages";
	}
}
