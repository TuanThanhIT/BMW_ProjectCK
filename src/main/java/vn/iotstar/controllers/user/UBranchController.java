package vn.iotstar.controllers.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.entity.Branch;
import vn.iotstar.entity.MilkTea;
import vn.iotstar.services.IBranchService;

@Controller
@RequestMapping("user/branch")
public class UBranchController {
	@Autowired
	private IBranchService branchService;

	@GetMapping
	public String ShowBranch(@RequestParam(required = false) String cityId,
			@RequestParam(required = false) String cityName, @Param("keyword") String keyword,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, Model model) {

		Page<Branch> listBranch = branchService.findAll(pageNo);

		// Kiểm tra nếu cityId hoặc cityName là null, rỗng hoặc "all" thì lấy tất cả chi
		// nhánh

		// Nếu cityName là null hoặc rỗng, tìm kiếm chỉ theo keyword
		if (cityName == null || cityName.isEmpty() ) {
			if (keyword != null) {
				listBranch = branchService.searchBranch(keyword, pageNo); // Tìm kiếm chỉ theo keyword
				model.addAttribute("keyword", keyword);
			} else {
				listBranch = branchService.findAll(pageNo); // Lấy tất cả chi nhánh nếu không có keyword
			}
		} else {
			// Nếu có cityName, tìm kiếm chi nhánh theo cityName và keyword (nếu có)
			if (keyword != null) {
				listBranch = branchService.searchBranchInCity(keyword, cityName, pageNo); // Tìm kiếm theo cityName và
																						// keyword
				model.addAttribute("keyword", keyword);
			} else {
				listBranch = branchService.findByAddressContaining(cityName, pageNo); // Tìm kiếm theo cityName
			}
		}
		model.addAttribute("totalPage", listBranch.getTotalPages());
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("cityId", cityId); // Thêm cityId vào model
		model.addAttribute("cityName", cityName);
		model.addAttribute("listBranch", listBranch);

		return "user/branch";
	}

	@GetMapping("detail/{id}")
	public String ShowBranchDetail(@PathVariable("id") Integer id, Model model) {
		Optional<Branch> OptBranch = branchService.findById(id);
		Branch branch = OptBranch.get();
		model.addAttribute("branch", branch);

		List<MilkTea> listMilkTea = branchService.findMilkTeaByBranchID(id);
		model.addAttribute("listMilkTeaOfBranch", listMilkTea);
		return "user/branchdetail";
	}

}