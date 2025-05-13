package vn.iotstar.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.iotstar.entity.Order;

import vn.iotstar.services.IOrderService;

@Controller
@RequestMapping("admin/order")
public class OrderController {
	
	@Autowired
	IOrderService iOrderService;
	@RequestMapping("")
	public String listCategories(ModelMap model) {
		List<Order> list = iOrderService.findAll();
		System.out.println("Danh sách orders: " + list); // Debug kiểm tra
		model.addAttribute("orders", list); // Tên biến là "milkTeaTypes"
		return "admin/order/list"; // Tên View
	}
}
