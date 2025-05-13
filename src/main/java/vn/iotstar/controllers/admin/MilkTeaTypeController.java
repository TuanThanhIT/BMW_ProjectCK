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
import vn.iotstar.entity.MilkTeaType;
import vn.iotstar.model.MilkTeaTypeModel;
import vn.iotstar.services.IMilkTeaTypeService;


@Controller
@RequestMapping("admin/milkTeaType")
public class MilkTeaTypeController {
	@Autowired
	IMilkTeaTypeService milkTeaTypeService;

//	@RequestMapping("")
//	public String listCategories(ModelMap model) {
//		List<MilkTeaType> list = milkTeaTypeService.findAll();
//		System.out.println("Danh sách MilkTeaType: " + list); // Debug kiểm tra
//		model.addAttribute("milkTeaTypes", list); // Tên biến là "milkTeaTypes"
//		return "admin/milkTeaType/list"; // Tên View
//	}

	
	 @RequestMapping("")
	    public String listCategories(@RequestParam(defaultValue = "0") int page, ModelMap model) {
	        // Sử dụng pageable để giới hạn số sản phẩm hiển thị trên mỗi trang
	        Pageable pageable = PageRequest.of(page, 5); // 5 sản phẩm mỗi trang
	        Page<MilkTeaType> milkTeaTypesPage = milkTeaTypeService.findAll(pageable);

	        // Thêm các thuộc tính vào model để truyền cho view
	        model.addAttribute("milkTeaTypes", milkTeaTypesPage.getContent()); // Dữ liệu sản phẩm
	        model.addAttribute("currentPage", page); // Trang hiện tại
	        model.addAttribute("totalPages", milkTeaTypesPage.getTotalPages()); // Tổng số trang
	        return "admin/milkTeaType/list"; // View name
	    }
	@GetMapping("add")
	public String add(ModelMap model) {
		MilkTeaTypeModel proModel = new MilkTeaTypeModel();
		proModel.setIsEdit(false);
		// chuyển dữ liệu từ model vào biến product để đưa lên view
		model.addAttribute("milkTeaType", proModel);
		return "admin/milkTeaType/addOrEdit";
	}
	
	

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(@Valid @ModelAttribute("milkTeaType") MilkTeaTypeModel milkTeaType,
	                                  BindingResult result, RedirectAttributes redirectAttributes) {
	    // Kiểm tra nếu có lỗi validate
	    if (result.hasErrors()) {
	        return new ModelAndView("admin/milkTeaType/addOrEdit"); // Nếu có lỗi, trở lại trang thêm hoặc sửa
	    }

	    // Kiểm tra milkTeaType không phải null
	    if (milkTeaType != null) {
	        MilkTeaType entity = new MilkTeaType(); // Sử dụng MilkTeaType thay vì Category

	        // Copy từ Model (milkTeaType) sang Entity (entity)
	        BeanUtils.copyProperties(milkTeaType, entity);

	        // Lưu hoặc cập nhật vào cơ sở dữ liệu
	        milkTeaTypeService.save(entity);

	        // Thông báo kết quả
	        String message = "";
	        if (milkTeaType.getIsEdit()) {
	            message = "Milk Tea Type đã được cập nhật thành công";
	        } else {
	            message = "Milk Tea Type đã được thêm thành công";
	        }

	        // Thêm thông báo vào redirectAttributes (Flash Attribute)
	        redirectAttributes.addFlashAttribute("message", message);

	        // Chuyển hướng về danh sách Milk Tea Types
	        return new ModelAndView("redirect:/admin/milkTeaType");
	    }

	    // Nếu milkTeaType null, trả về lại form thêm hoặc sửa
	    return new ModelAndView("admin/milkTeaType/addOrEdit");
	}


	@GetMapping("edit/{milkTeaTypeId}")
	public ModelAndView edit(ModelMap model, @PathVariable("milkTeaTypeId") Integer milkTeaTypeId) {
		Optional<MilkTeaType> opt = milkTeaTypeService.findById(milkTeaTypeId);

		MilkTeaTypeModel milkTeaTypeModel = new MilkTeaTypeModel();
		if (opt.isPresent()) {
			MilkTeaType entity = opt.get();
			// Copy properties from entity to model
			BeanUtils.copyProperties(entity, milkTeaTypeModel);

			milkTeaTypeModel.setIsEdit(true);

			model.addAttribute("milkTeaType", milkTeaTypeModel);

			return new ModelAndView("admin/milkTeaType/addOrEdit", model);
		}

		model.addAttribute("message", "Milk Tea Type không tồn tại");
		return new ModelAndView("forward:/admin/milkTeaType", model);
	}
	@GetMapping("delete/{milkTeaTypeID}")
	public ModelAndView delete(@PathVariable("milkTeaTypeID") Integer milkTeaTypeID, 
	                           RedirectAttributes redirectAttributes) {
	    // Xóa MilkTeaType theo ID
	    milkTeaTypeService.deleteById(milkTeaTypeID);
	    
	    // Thêm thông báo vào RedirectAttributes
	    redirectAttributes.addFlashAttribute("message", "Milk Tea Type đã được xóa thành công");
	    
	    // Redirect về danh sách Milk Tea Types
	    return new ModelAndView("redirect:/admin/milkTeaType");
	}


//	@GetMapping("edit/{idType}")
//	public ModelAndView edit(ModelMap model, @PathVariable("idType") int idType) {
//		Optional<MilkTeaTypeEntity> opt = milkTeaTypeService.findById(idType);
//		MilkTeaTypeModel milkTea = new MilkTeaTypeModel();
//		if (opt.isPresent()) {
//			MilkTeaTypeEntity entity = opt.get();
//			BeanUtils.copyProperties(entity, milkTea);
//			milkTea.setIsEdit(true);
//			model.addAttribute("milkTeaType", milkTea);
//			return new ModelAndView("admin/customize/customize-milk-tea-type", model);
//		}
//
//		model.addAttribute("message", "Type không tồn tại");
//		return new ModelAndView("forward:/admin/milk-tea", model);
//	}
}
