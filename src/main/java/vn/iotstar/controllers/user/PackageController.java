package vn.iotstar.controllers.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

	public static User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
			return null;
		}
		return (User) auth.getPrincipal();
	}

	@GetMapping({ "", "/" })
	public String getOrder(HttpSession session, Model model)
	{
		User user = getCurrentUser();
  		if (user == null) return "redirect:/login";

	    // Tìm các đơn hàng theo User ID
	    List<Order> orders = orderServ.findByUserId(user.getUserID());

	    // Đưa danh sách đơn hàng vào model
	    model.addAttribute("orders", orders);
		
		return "user/packages";
	}
}
