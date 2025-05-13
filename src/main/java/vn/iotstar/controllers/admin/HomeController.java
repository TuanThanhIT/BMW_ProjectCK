package vn.iotstar.controllers.admin;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.iotstar.services.IBranchService;
import vn.iotstar.services.ICartService;
import vn.iotstar.services.IIncomeService;
import vn.iotstar.services.IUserService;

@Controller
@RequestMapping("admin")
public class HomeController {
	@Autowired
	IUserService iUserService;

	@Autowired
	IIncomeService iIncomeService;

	@Autowired
	IBranchService branchService;

	@Autowired
	ICartService cartService;

	@RequestMapping("")
	public String listCategories(ModelMap model) {
		// Lấy thông tin user từ session trực tiếp

		long countUser = iUserService.countByRole("USER");
		model.addAttribute("countUser", countUser);

		double totalIncome = iIncomeService.sumIncomeValue();

		// Định dạng số với đơn vị VNĐ
		NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
		String formattedIncome = format.format(totalIncome) + " VNĐ";
		model.addAttribute("income", formattedIncome); // Thêm vào model

		long totalBranch = branchService.count();
		model.addAttribute("totalBranch", totalBranch); // Thêm vào model

		long totalCart = cartService.getTotalProductCount();
		model.addAttribute("totalCart", totalCart); // Thêm vào model

		//////////// session chart//////////////
		// pie chart
		long countSeller = iUserService.countByRole("SELLER");
		model.addAttribute("countSeller", countSeller);
		long countShipper = iUserService.countByRole("SHIPPER");
		model.addAttribute("countShipper", countShipper);

		// line graph
		int[] months = new int[12];
		double[] totalIncomePerMonth = new double[12];

		// Khởi tạo tháng và doanh thu mặc định là 0
		for (int i = 0; i < 12; i++) {
			months[i] = i + 1;
			totalIncomePerMonth[i] = 0;
		}

		// Giả sử đây là dữ liệu lấy từ cơ sở dữ liệu
		List<Object[]> monthlyIncome = iIncomeService.findMonthlyIncome(2024);

		// Gán doanh thu vào các tháng tương ứng
		for (Object[] row : monthlyIncome) {
			int month = (int) row[0]; // Lấy tháng
			double income = (double) row[1]; // Lấy doanh thu
			totalIncomePerMonth[month - 1] = income; // Gán doanh thu vào tháng tương ứng
		}

		// Chuyển mảng thành chuỗi JSON để sử dụng trong view
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String monthsJson = objectMapper.writeValueAsString(months);
			String totalIncomeJson = objectMapper.writeValueAsString(totalIncomePerMonth);

			// Thêm dữ liệu vào model
			model.addAttribute("monthsJson", monthsJson);
			model.addAttribute("totalIncomeJson", totalIncomeJson);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
////////////////////////////////////////////
		// Lấy dữ liệu doanh thu theo chi nhánh từ cơ sở dữ liệu
		List<Object[]> branchIncome = iIncomeService.findTotalIncomeByBranch();

		// Tạo danh sách chứa tên chi nhánh và doanh thu
		List<Map<String, Object>> branchIncomeData = new ArrayList<>();
		if (branchIncome != null && !branchIncome.isEmpty()) {
		    for (Object[] row : branchIncome) {
		        Map<String, Object> data = new HashMap<>();
		        data.put("branchId", (int) row[0]);  // ID chi nhánh
		        data.put("income", (double) row[1]); // Doanh thu
		        branchIncomeData.add(data);
		    }
		}

		// Tách ra thành 2 phần: chi nhánh và doanh thu
		List<String> branchLabels = new ArrayList<>();
		List<Double> branchIncomes = new ArrayList<>();
		for (Map<String, Object> data : branchIncomeData) {
		    branchLabels.add("Chi nhánh " + data.get("branchId"));
		    branchIncomes.add((Double) data.get("income"));
		}

		// Chuyển mảng thành chuỗi JSON để sử dụng trong view
		ObjectMapper objectMapper2 = new ObjectMapper();
		try {
		    String branchLabelsJson = objectMapper2.writeValueAsString(branchLabels);
		    String branchIncomesJson = objectMapper2.writeValueAsString(branchIncomes);

		    // Thêm dữ liệu vào model
		    model.addAttribute("branchLabelsJson", branchLabelsJson);
		    model.addAttribute("branchIncomesJson", branchIncomesJson);

		} catch (JsonProcessingException e) {
		    e.printStackTrace();
		}
		return "admin/index"; // Tên View
	}
}
