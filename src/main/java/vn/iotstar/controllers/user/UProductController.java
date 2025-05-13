package vn.iotstar.controllers.user;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Like;
import vn.iotstar.entity.MilkTea;
import vn.iotstar.entity.MilkTeaType;
import vn.iotstar.entity.Rate;
import vn.iotstar.entity.User;
import vn.iotstar.services.IBranchMilkTeaService;
import vn.iotstar.services.ILikeService;
import vn.iotstar.services.IMilkTeaService;
import vn.iotstar.services.IMilkTeaTypeService;
import vn.iotstar.services.IRateService;
import vn.iotstar.services.IUserService;

@Controller
@RequestMapping("user/product")
public class UProductController {
	@Autowired
	private IMilkTeaTypeService milkteaType;

	@Autowired
	private IMilkTeaService milkTeaService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IRateService rateService;

	@Autowired
	private ILikeService likeService;
	
	@Autowired
	private IBranchMilkTeaService branchMilkTeaService;

	@GetMapping
	public String ShowProductPage(@RequestAttribute(name = "user", required = false) User user,
	                               @RequestParam(required = false) Integer typeMilkTeaID,
	                               @Param("keyword") String keyword,
	                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
	                               Model model) {
	    if (user == null) {
	        // Nếu không có thông tin user, điều hướng người dùng tới trang đăng nhập
	        return "redirect:/login"; // Ví dụ: Redirect về trang đăng nhập
	    }

	    // Tiến hành xử lý trang sản phẩm với thông tin người dùng đã lấy
	    List<MilkTeaType> list = milkteaType.findAll();
	    model.addAttribute("listType", list);

	    // Xử lý việc tìm kiếm và phân trang
	    Page<MilkTea> list1;
	    if (keyword != null) {
	        if (typeMilkTeaID == null) {
	            list1 = milkTeaService.searchMilkTea(keyword, pageNo); // Tìm kiếm theo keyword
	        } else {
	            list1 = milkTeaService.searchMilkTeaByType(keyword, typeMilkTeaID, pageNo); // Tìm kiếm theo typeMilkTeaID và keyword
	        }
	    } else {
// Nếu không có keyword, hiển thị tất cả sản phẩm của loại trà sữa (nếu có typeMilkTeaID)
	        if (typeMilkTeaID == null) {
	            list1 = milkTeaService.findAll(pageNo);
	        } else {
	            list1 = milkTeaService.findByMilkTeaType_MilkTeaTypeID(typeMilkTeaID, pageNo);
	        }
	    }

	    // Lấy danh sách trà sữa và cập nhật trạng thái yêu thích
	    for (MilkTea milkTea : list1) {
	        Optional<Like> existingLike = likeService.findByUserAndMilkTea(user, milkTea);
	        milkTea.setFavorited(existingLike.isPresent());
	    }

	    model.addAttribute("totalPage", list1.getTotalPages());
	    model.addAttribute("currentPage", pageNo);
	    model.addAttribute("typeMilkTeaID", typeMilkTeaID);
	    model.addAttribute("listMilkTea", list1);
	    model.addAttribute("keyword", keyword); // Đảm bảo keyword cũng được truyền vào view

	    return "user/product";
	}

	@GetMapping("detail/{id}")
	public String ShowProductDetail(@PathVariable("id") Integer id, Model model) {
		int tongsao = 0;
		BigDecimal saotb;
		Optional<MilkTea> OptMilkTea = milkTeaService.findById(id);
		MilkTea milkTea = OptMilkTea.get();
		
		List<Rate> listRate = rateService.findByMilkTea(milkTea);
		int numberOfRates = listRate.size();
		if (numberOfRates == 0) {

			saotb = BigDecimal.valueOf(0);
		} else {

			for (Rate rate : listRate) {
				tongsao = tongsao + rate.getRateValue().intValue();
			}
			saotb = new BigDecimal(tongsao / (double) numberOfRates); // Ép kiểu để chia chính xác
			saotb = saotb.setScale(1, RoundingMode.HALF_UP); // Làm tròn đến 1 chữ số thập phân
		}
		int sellQuantity = branchMilkTeaService.findSellQuantityByMilkTeaID(id);
		List<MilkTea> listMilkTeaRelateTo = milkTeaService.findByMilkTeaTypeAndMilkTeaIDNot(milkTea.getMilkTeaType(),id);
		model.addAttribute("numberOfRates", numberOfRates);
		model.addAttribute("milkTea", milkTea);
		model.addAttribute("listRate", listRate);
		model.addAttribute("saotb", saotb);
		model.addAttribute("listMilkTeaRelateTo", listMilkTeaRelateTo);
		model.addAttribute("sellQuantity", sellQuantity);
		return "user/productdetail";
	}

	@PostMapping("rate")
	public String handleRateMilkTea(@RequestParam("rating") BigDecimal rateValue,
			@RequestParam("comment") String comment, @RequestParam("milkTeaID") int milkTeaID,
			@RequestParam("userID") int userID) {
		LocalDate createdAt = LocalDate.now();
		Optional<MilkTea> OptMilkTea = milkTeaService.findById(milkTeaID);
		Optional<User> OptUser = userService.findById(userID);

		if (OptMilkTea.isPresent() && OptUser.isPresent()) {
			MilkTea milkTea = OptMilkTea.get();
			User user = OptUser.get();

			Rate rate = new Rate();
			rate.setComment(comment);
			rate.setRateValue(rateValue);
			rate.setPostTime(createdAt);
			rate.setMilkTea(milkTea);
			rate.setUser(user);

			rateService.save(rate);
		} else {
			// Xử lý khi không tìm thấy MilkTea hoặc User
			return "error"; // Hoặc trang lỗi phù hợp
		}

		// Redirect với id của MilkTea sau khi thêm đánh giá
		return "redirect:/user/product/detail/" + milkTeaID;

	}
	@GetMapping("favorite/{id}")
	public String ShowLikePage(@PathVariable("id") Integer userID, Model model, HttpSession session) {
		User loggedInUser = (User) session.getAttribute("account");
        if (!userID.equals(loggedInUser.getUserID())) {
            model.addAttribute("message", "Bạn không có quyền truy cập!");
            return "user/error"; // Hoặc trả về trang lỗi
        }
		
		List<MilkTea> list = likeService.getLikedMilkTeas(userID);
		for (MilkTea milkTea : list) {
			// Kiểm tra sản phẩm đã được yêu thích hay chưa
			milkTea.setFavorited(true); // Cập nhật trạng thái yêu thích
		}
		model.addAttribute("favoriteProducts", list);
		return "user/productlike";
	}

	@PostMapping("/api/favorite")
	@ResponseBody
	public Map<String, Object> toggleFavorite(@RequestBody Map<String, Object> requestData) {
		Map<String, Object> response = new HashMap<>();
		try {
			int userID = Integer.parseInt(requestData.get("userID").toString());
			int milkTeaID = Integer.parseInt(requestData.get("milkTeaID").toString());

			User user = userService.findById(userID).orElseThrow(() -> new RuntimeException("User không tồn tại"));
			MilkTea milkTea = milkTeaService.findById(milkTeaID)
					.orElseThrow(() -> new RuntimeException("Milk Tea không tồn tại"));

			Optional<Like> existingLike = likeService.findByUserAndMilkTea(user, milkTea);
			if (existingLike.isPresent()) {
				likeService.deleteByUserAndMilkTea(user, milkTea);
				milkTea.setFavorited(false); // Cập nhật trạng thái yêu thích
				response.put("action", "unliked");
			} else {
				Like like = new Like();
				like.setUser(user);
				like.setMilkTea(milkTea);
				like.setLikedAt(LocalDate.now());
				likeService.save(like);
				milkTea.setFavorited(true); // Cập nhật trạng thái yêu thích
				response.put("action", "liked");
			}

			response.put("success", true);
		} catch (Exception e) {
			response.put("success", false);
			response.put("error", e.getMessage());
		}
		return response;
	}
	
	

}