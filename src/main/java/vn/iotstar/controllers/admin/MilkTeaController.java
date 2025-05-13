package vn.iotstar.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.entity.MilkTea;
import vn.iotstar.services.IMilkTeaService;

@Controller
@RequestMapping("admin/milkTea")
public class MilkTeaController {
	@Autowired
	IMilkTeaService milkTeaService;
	
	@RequestMapping("")
    public String listMiilTea(@RequestParam(defaultValue = "0") int page, ModelMap model) {
        // Sử dụng pageable để giới hạn số sản phẩm hiển thị trên mỗi trang
        Pageable pageable = PageRequest.of(page, 5); // 5 sản phẩm mỗi trang
        Page<MilkTea> milkTeasPage = milkTeaService.findAll(pageable);

        // Thêm các thuộc tính vào model để truyền cho view
        model.addAttribute("milkTeas", milkTeasPage.getContent()); // Dữ liệu sản phẩm
        model.addAttribute("currentPage", page); // Trang hiện tại
        model.addAttribute("totalPages", milkTeasPage.getTotalPages()); // Tổng số trang
        return "admin/milkTea/list"; // View name
    }
	
	// Phương thức tìm kiếm milk tea và hiển thị danh sách
    @GetMapping("search")
    public String searchMilkTeas(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, Model model) {
        // Gọi service để tìm kiếm theo từ khóa
        List<MilkTea> milkTeas = milkTeaService.findByMilkTeaNameContaining(keyword);
        
        // Thêm danh sách kết quả vào model để truyền tới view
        model.addAttribute("milkTeas", milkTeas);
        model.addAttribute("keyword", keyword);  // Truyền lại từ khóa tìm kiếm cho người dùng

        return "admin/milkTea/list";  // Trả về trang list trong thư mục admin/milkTea
    }
	
}
