package vn.iotstar.controllers.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.iotstar.entity.Branch;
import vn.iotstar.services.IBranchService;

@Controller
@RequestMapping("admin/branch")
public class BranchController {
	@Autowired
	IBranchService ibranch;
	
	@RequestMapping("")
	public String listCategories(ModelMap model) 
	{
		List<Branch> list = ibranch.findAll();
		System.out.println("Danh sách users: " + list); // Debug kiểm tra
		model.addAttribute("branches", list); // Tên biến là "milkTeaTypes"
		return "admin/branch/list"; // Tên View
	}
	

	@RequestMapping("toggleActive/{branchID}")
	public String toggleBranchActive(@PathVariable Integer branchID, ModelMap model) {
	    try {
	        Optional<Branch> branchOpt = ibranch.findById(branchID); // Tìm chi nhánh
	        if (branchOpt.isPresent()) {
	            Branch branch = branchOpt.get();
	            branch.setActive(!branch.isActive()); // Đổi trạng thái
	            ibranch.save(branch); // Lưu vào DB

	            // Thêm thông báo vào ModelMap và quay lại trang danh sách
	            model.addAttribute("message", "Trạng thái chi nhánh đã được thay đổi!");
	            return "forward:/admin/branch"; // Quay lại trang danh sách các chi nhánh
	        } else {
	            model.addAttribute("message", "Không tìm thấy chi nhánh có ID: " + branchID);
	            return "redirect:/admin/branch"; // Quay lại trang danh sách nếu không tìm thấy chi nhánh
	        }
	    } catch (Exception e) {
	        model.addAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
	        return "redirect:/admin/branch"; // Quay lại trang danh sách nếu có lỗi
	    }
	}
	
	 @GetMapping("/delete/{id}")
	    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
	        try {
	            ibranch.deleteById(id);
	            redirectAttributes.addFlashAttribute("message", "Branch xóa thành công!");
	        } catch (Exception e) {
	            redirectAttributes.addFlashAttribute("message", 
	                "Không thể xóa branch. Branch còn tồn tại trong BranchMilkTea.");
	        }
	        return "redirect:/admin/branch";
	    }
}
