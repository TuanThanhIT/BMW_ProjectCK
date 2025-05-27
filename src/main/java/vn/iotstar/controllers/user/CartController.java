package vn.iotstar.controllers.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.entity.Cart;
import vn.iotstar.entity.CartMilkTea;
import vn.iotstar.entity.MilkTea;
import vn.iotstar.entity.Sizes;
import vn.iotstar.entity.User;

import vn.iotstar.services.ICartMilkTeaService;
import vn.iotstar.services.ICartService;
import vn.iotstar.services.IMilkTeaService;
import vn.iotstar.services.ISizeService;

@Controller
@RequestMapping("user/cart")
public class CartController {
	@Autowired
	private IMilkTeaService milkTeaServ;
	@Autowired
	private ICartService cartServ;
	@Autowired
	private ICartMilkTeaService cmilkTeaServ;

	@Autowired
	private ISizeService sizeServ;

	public static User getCurrentUser() {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
          return null;
      }
      return (User) auth.getPrincipal();
  	}

	@GetMapping("/addToCart")
	public String addToCart(@RequestParam int id, @RequestParam int quantity, @RequestParam String size, Model model) {
		
		User user = getCurrentUser();
  		if (user == null) return "redirect:/login";

		// Tìm giỏ hàng theo User ID
		Cart cart = cartServ.findByUserId1(user.getUserID()).orElse(null);
		if (cart == null) {
			cart = new Cart();
			cart.setUser(user);
			cart.setMilkTeas(new ArrayList<>());
			cartServ.save(cart);
		}

		// Tìm sản phẩm MilkTea
		MilkTea milkTea = milkTeaServ.findById(id).orElseThrow(() -> new RuntimeException("MilkTea không tồn tại"));

		// Tìm kích thước Size
		Sizes milkSize = sizeServ.findByName(size);
		if (milkSize == null) {
			throw new RuntimeException("Kích thước không hợp lệ");
		}

		// Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng chưa
		Optional<CartMilkTea> existingCartMilkTea = cart.getMilkTeas().stream().filter(
				cmt -> cmt.getMilkTea().getMilkTeaID() == id && cmt.getSize().getSizeID() == milkSize.getSizeID())
				.findFirst();

		if (existingCartMilkTea.isPresent()) {
			// Nếu sản phẩm đã tồn tại, tăng số lượng
			CartMilkTea cartMilkTea = existingCartMilkTea.get();
			cartMilkTea.setQuantityMilkTea(cartMilkTea.getQuantityMilkTea() + quantity);
		} else {
			// Nếu sản phẩm chưa tồn tại, thêm sản phẩm mới vào giỏ hàng
			CartMilkTea cartMilkTea = new CartMilkTea();
			cartMilkTea.setMilkTea(milkTea);
			cartMilkTea.setCart(cart);
			cartMilkTea.setQuantityMilkTea(quantity);
			cartMilkTea.setSize(milkSize);
			cart.getMilkTeas().add(cartMilkTea);
		}

		// Cập nhật tổng số lượng sản phẩm và tổng giá tiền của giỏ hàng
		cart.setTotalProduct(cart.getMilkTeas().stream().mapToInt(CartMilkTea::getQuantityMilkTea).sum());
		cart.setTotalCost(cart.getMilkTeas().stream()
				.map(cmt -> cmt.getMilkTea().getDiscountPrice().add(cmt.getSize().getExtraCost())
						.multiply(BigDecimal.valueOf(cmt.getQuantityMilkTea())))
				.reduce(BigDecimal.ZERO, BigDecimal::add));

		// Lưu lại giỏ hàng
		cartServ.save(cart);

		// Chuyển hướng về trang giỏ hàng
		return "redirect:/user/cart";
	}

	@GetMapping({ "", "/" })
	public String cartGet(Model model) {

		User user = getCurrentUser();

		if (user == null) {
			// Nếu không có user trong session, chuyển hướng đến trang đăng nhập hoặc xử lý
			// phù hợp
			return "redirect:/login";
		}

		// Tìm kiếm Cart bằng User ID
		Optional<Cart> cartOptional = cartServ.findByUserId1(user.getUserID());
		Cart cart;

		if (cartOptional.isPresent()) {
			// Nếu Cart tồn tại
			cart = cartOptional.get();
		} else {
			// Nếu không tìm thấy Cart, tạo mới Cart
			cart = new Cart();
			cart.setUser(user);
			cart.setMilkTeas(new ArrayList<>());
			cartServ.save(cart); // Lưu Cart mới vào cơ sở dữ liệu
		}

		// Đưa dữ liệu giả vào model
		model.addAttribute("listCart", cart.getMilkTeas());
		model.addAttribute("total", cart.getTotalCost());

		return "user/cart";
	}

	@GetMapping("/remove/{id}")
	public String removeItem(@PathVariable("id") int id) {

		User user = getCurrentUser();
  		if (user == null) return "redirect:/login";

		// Tìm giỏ hàng theo User ID
		Cart cart = cartServ.findByUserId1(user.getUserID())
				.orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));

		// Tìm sản phẩm cần xóa (CartMilkTea) theo ID
		CartMilkTea cartMilkTea = cmilkTeaServ.findById(id)
				.orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại trong giỏ hàng"));

		// Kiểm tra sản phẩm có thuộc giỏ hàng của người dùng hay không
		if (!cart.getMilkTeas().contains(cartMilkTea)) {
			throw new RuntimeException("Sản phẩm không thuộc giỏ hàng của người dùng");
		}

		// Xóa sản phẩm khỏi danh sách trong giỏ hàng
		cart.getMilkTeas().remove(cartMilkTea);

		// Cập nhật lại tổng giá trị giỏ hàng
		BigDecimal newTotal = cart.getMilkTeas().stream().map(CartMilkTea::getTotalPrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		cart.setTotalCost(newTotal);

		// Cập nhật lại số lượng sản phẩm trong giỏ hàng
		cart.setTotalProduct(cart.getMilkTeas().size()); // Cập nhật số lượng sản phẩm trong giỏ hàng

		// Xóa sản phẩm khỏi cơ sở dữ liệu
		System.out.println(id);
		cmilkTeaServ.deleteByIdCustom(id); // Xóa sản phẩm khỏi cơ sở dữ liệu

		// Lưu lại giỏ hàng sau khi cập nhật
		cartServ.save(cart);

		// Chuyển hướng về trang giỏ hàng
		return "redirect:/user/cart";
	}

}
