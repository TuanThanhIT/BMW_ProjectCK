package vn.iotstar.controllers.admin;

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
import vn.iotstar.entity.Delivery;
import vn.iotstar.model.DeliveryModel;
import vn.iotstar.services.IDeliveryService;

@ Controller
@RequestMapping("admin/delivery")
public class DeliveryController {
	@Autowired
	IDeliveryService deliveryService;
	 @RequestMapping("")
	    public String listCategories(@RequestParam(defaultValue = "0") int page, ModelMap model) {
	        // Sử dụng pageable để giới hạn số sản phẩm hiển thị trên mỗi trang
	        Pageable pageable = PageRequest.of(page, 5); // 5 sản phẩm mỗi trang
	        Page<Delivery> deliveryPage = deliveryService.findAll(pageable);

	        // Thêm các thuộc tính vào model để truyền cho view
	        model.addAttribute("deliveries", deliveryPage.getContent()); // Dữ liệu sản phẩm
	        model.addAttribute("currentPage", page); // Trang hiện tại
	        model.addAttribute("totalPages", deliveryPage.getTotalPages()); // Tổng số trang
	        return "admin/delivery/list"; // View name
	    }
	 @GetMapping("add")
		public String add(ModelMap model) {
			DeliveryModel proModel = new DeliveryModel();
			proModel.setIsEdit(false);
			// chuyển dữ liệu từ model vào biến product để đưa lên view
			model.addAttribute("delivery", proModel);
			return "admin/delivery/addOrEdit";
		}
		
		

		@PostMapping("saveOrUpdate")
		public ModelAndView saveOrUpdate(@Valid @ModelAttribute("delivery") DeliveryModel delivery,
		                                  BindingResult result, RedirectAttributes redirectAttributes) {
		    // Kiểm tra nếu có lỗi validate
		    if (result.hasErrors()) {
		        return new ModelAndView("admin/delivery/addOrEdit"); // Nếu có lỗi, trở lại trang thêm hoặc sửa
		    }

		    // Kiểm tra delivery không phải null
		    if (delivery != null) {
		        Delivery entity = new Delivery(); // Sử dụng Delivery thay vì Category

		        // Copy từ Model (milkTeaType) sang Entity (entity)
		        BeanUtils.copyProperties(delivery, entity);

		        // Lưu hoặc cập nhật vào cơ sở dữ liệu
		        deliveryService.save(entity);

		        // Thông báo kết quả
		        String message = "";
		        if (delivery.getIsEdit()) {
		            message = "Milk Tea Type đã được cập nhật thành công";
		        } else {
		            message = "Milk Tea Type đã được thêm thành công";
		        }

		        // Thêm thông báo vào redirectAttributes (Flash Attribute)
		        redirectAttributes.addFlashAttribute("message", message);

		        // Chuyển hướng về danh sách Milk Tea Types
		        return new ModelAndView("redirect:/admin/delivery");
		    }

		    // Nếu Delivery null, trả về lại form thêm hoặc sửa
		    return new ModelAndView("admin/delivery/addOrEdit");
		}


		@GetMapping("edit/{deliveryId}")
		public ModelAndView edit(ModelMap model, @PathVariable("deliveryId") Integer deliveryId) {
			Optional<Delivery> opt = deliveryService.findById(deliveryId);

			DeliveryModel DeliveryModel = new DeliveryModel();
			if (opt.isPresent()) {
				Delivery entity = opt.get();
				// Copy properties from entity to model
				BeanUtils.copyProperties(entity, DeliveryModel);

				DeliveryModel.setIsEdit(true);

				model.addAttribute("delivery", DeliveryModel);

				return new ModelAndView("admin/delivery/addOrEdit", model);
			}

			model.addAttribute("message", "Milk Tea Type không tồn tại");
			return new ModelAndView("forward:/admin/delivery", model);
		}
		@GetMapping("delete/{deliveryId}")
		public ModelAndView delete(@PathVariable("deliveryId") Integer deliveryId, 
		                           RedirectAttributes redirectAttributes) {
		    // Xóa MilkTeaType theo ID
		    deliveryService.deleteById(deliveryId);
		    
		    // Thêm thông báo vào RedirectAttributes
		    redirectAttributes.addFlashAttribute("message", "Milk Tea Type đã được xóa thành công");
		    
		    // Redirect về danh sách Milk Tea Types
		    return new ModelAndView("redirect:/admin/delivery");
		}
}
