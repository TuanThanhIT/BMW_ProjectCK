package vn.iotstar.controllers.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import vn.iotstar.entity.Branch;
import vn.iotstar.entity.Delivery;
import vn.iotstar.entity.MilkTeaType;
import vn.iotstar.entity.Shipper;
import vn.iotstar.entity.User;
import vn.iotstar.model.MilkTeaTypeModel;
import vn.iotstar.model.ShipperModel;
import vn.iotstar.services.IBranchService;
import vn.iotstar.services.IDeliveryService;
import vn.iotstar.services.IMilkTeaTypeService;
import vn.iotstar.services.IShipperService;
import vn.iotstar.services.IUserService;

@Controller
@RequestMapping("admin/shipper")
public class ShipperController {

//	@RequestMapping("")
//	public String listCategories(ModelMap model) {
//		List<MilkTeaType> list = milkTeaTypeService.findAll();
//		System.out.println("Danh sách MilkTeaType: " + list); // Debug kiểm tra
//		model.addAttribute("milkTeaTypes", list); // Tên biến là "milkTeaTypes"
//		return "admin/milkTeaType/list"; // Tên View
//	}
	@Autowired
	IShipperService milkTeaTypeService; // luoi sua nen đặt tên cũ luôn
	@Autowired
	IUserService userService;

	@RequestMapping("")
	public String listCategories(@RequestParam(defaultValue = "0") int page, ModelMap model) {
		// Sử dụng pageable để giới hạn số sản phẩm hiển thị trên mỗi trang
		Pageable pageable = PageRequest.of(page, 5); // 5 sản phẩm mỗi trang
		Page<Shipper> milkTeaTypesPage = milkTeaTypeService.findAll(pageable);

		// Thêm các thuộc tính vào model để truyền cho view
		model.addAttribute("shippers", milkTeaTypesPage.getContent()); // Dữ liệu sản phẩm
		model.addAttribute("currentPage", page); // Trang hiện tại
		model.addAttribute("totalPages", milkTeaTypesPage.getTotalPages()); // Tổng số trang
		return "admin/shipper/list"; // View name
	}

	@GetMapping("add")
	public String add(ModelMap model) {
		ShipperModel proModel = new ShipperModel();
		proModel.setIsEdit(false);
		// chuyển dữ liệu từ model vào biến product để đưa lên view
		List<User> users = userService.findByRoleID(4);  // Lấy tất cả người dùng
        List<Branch> branches = branchService.findAll();  // Lấy tất cả chi nhánh

        model.addAttribute("users", users);   // Danh sách người dùng
        model.addAttribute("branches", branches);  // Danh sách chi nhánh
        model.addAttribute("deliveries", deliveryService.findAll()); // Danh sách nhà vận chuyển

		model.addAttribute("shipper", proModel);
		return "admin/shipper/addOrEdit";
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(@Valid @ModelAttribute("shipper") ShipperModel shipper,
	                                 BindingResult result, RedirectAttributes redirectAttributes) {
	    // Kiểm tra nếu có lỗi validate
	    if (result.hasErrors()) {
	        return new ModelAndView("admin/shipper/addOrEdit"); // Nếu có lỗi, trở lại trang thêm hoặc sửa
	    }
	    System.out.println("ShipperModel received: " + shipper);

	    // Kiểm tra shipper không phải null
	    if (shipper != null) {
	        Shipper entity;

	        // Nếu đang chỉnh sửa, lấy Shipper từ DB hoặc tạo mới nếu không tìm thấy
	        if (shipper.getIsEdit()) {
	            Optional<Shipper> existingShipper = milkTeaTypeService.findById(shipper.getShipID());
	            if (existingShipper.isPresent()) {
	                entity = existingShipper.get();
	            } else {
	                // Nếu không tìm thấy, tạo mới
	                entity = new Shipper();
	            }
	        } else {
	            // Nếu chưa có shipper, tạo mới
	            entity = new Shipper();
	        }

	        // Truy xuất Branch từ DB và gán vào Entity
	        Optional<Branch> branchOpt = branchService.findById(shipper.getBranchID());
	        if (branchOpt.isPresent()) {
	            entity.setBranch(branchOpt.get());  // Gán đối tượng Branch vào Entity
	        } else {
	            throw new IllegalArgumentException("Branch ID không hợp lệ!");
	        }

	        // Truy xuất Delivery từ DB và gán vào Entity
	        Optional<Delivery> deliveryOpt = deliveryService.findById(shipper.getDeliveryID());
	        if (deliveryOpt.isPresent()) {
	            entity.setDelivery(deliveryOpt.get());  // Gán đối tượng Delivery vào Entity
	        } else {
	            throw new IllegalArgumentException("Delivery ID không hợp lệ!");
	        }
	        
	        Optional<User> userOpt = userService.findById(shipper.getUserId());
	        if (userOpt.isPresent()) {
	            entity.setUser(userOpt.get());  
	        } else {
	            throw new IllegalArgumentException("User ID không hợp lệ!");
	        }

	        // Lưu đối tượng Shipper vào cơ sở dữ liệu
	        milkTeaTypeService.save(entity);

	        // Thông báo kết quả
	        String message = shipper.getIsEdit() ? "Shipper đã được cập nhật thành công" : "Shipper đã được thêm thành công";
	        redirectAttributes.addFlashAttribute("message", message);

	        // Chuyển hướng về danh sách Shippers
	        return new ModelAndView("redirect:/admin/shipper");
	    }

	    // Nếu shipper null, trả về lại form thêm hoặc sửa
	    return new ModelAndView("admin/shipper/addOrEdit");

	}

	@Autowired
	IShipperService shipperService;

	@Autowired
	IBranchService branchService;

	@Autowired
	IDeliveryService deliveryService;

	@GetMapping("edit/{shipID}")
	public String edit(ModelMap model, @PathVariable("shipID") Integer shipID) {
	    Optional<Shipper> optionalShipper = shipperService.findById(shipID);

	    if (optionalShipper.isPresent()) {
	        Shipper entity = optionalShipper.get();
	        ShipperModel shipperModel = new ShipperModel();
	        BeanUtils.copyProperties(entity, shipperModel);

	        // Gán thông tin bổ sung
	        if (entity.getUser() != null) {
	            shipperModel.setUserName(entity.getUser().getUserName());
	        }
	        if (entity.getBranch() != null) {
	            shipperModel.setBranchID(entity.getBranch().getBranchID());
	        }
	        if (entity.getDelivery() != null) {
	            shipperModel.setDeliveryID(entity.getDelivery().getDeliveryID());
	        }
	        if (entity.getRate() != null) {
	            shipperModel.setRateValue(entity.getRate().getRateValue());
	        }if (entity.getRate() != null) {
	            shipperModel.setRateValue(entity.getRate().getRateValue());
	        } else {
	            shipperModel.setRateValue(BigDecimal.valueOf(3)); // Chuyển đổi giá trị 3 thành BigDecimal
	        }


	        shipperModel.setIsEdit(true); // Đánh dấu là chế độ Edit
	        model.addAttribute("shipper", shipperModel);

	        // Truyền danh sách Users, Branches và Deliveries vào model
	        model.addAttribute("users", userService.findByRoleID(4)); // Danh sách người dùng
	        model.addAttribute("branches", branchService.findAll()); // Danh sách chi nhánh
	        model.addAttribute("deliveries", deliveryService.findAll()); // Danh sách nhà vận chuyển

	        return "admin/shipper/addOrEdit";
	    }

	    model.addAttribute("message", "Shipper không tồn tại!");
	    return "redirect:/admin/shipper";
	}

	   // Xóa shipper
    @GetMapping("/delete/{shipID}")
    public ModelAndView delete(@PathVariable("shipID") Integer shipID, 
            RedirectAttributes redirectAttributes) {
        try {
            shipperService.deleteById(shipID);
            redirectAttributes.addFlashAttribute("message", "Shipper đã được xóa thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Có lỗi xảy ra khi xóa shipper");
        }
        return new ModelAndView("redirect:/admin/shipper");
    }

}
