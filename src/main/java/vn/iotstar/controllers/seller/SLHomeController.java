package vn.iotstar.controllers.seller;

import java.awt.image.BufferedImage;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Branch;
import vn.iotstar.entity.BranchMilkTea;
import vn.iotstar.entity.MilkTea;
import vn.iotstar.entity.MilkTeaType;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.User;
import vn.iotstar.enums.OrderStatus;
import vn.iotstar.model.BranchDto;
import vn.iotstar.model.MilkTeaDto;
import vn.iotstar.services.IBranchMilkTeaService;
import vn.iotstar.services.IBranchService;
import vn.iotstar.services.IMilkTeaService;
import vn.iotstar.services.IMilkTeaTypeService;
import vn.iotstar.services.IOrderService;
import vn.iotstar.utils.PathConstants;

@Controller
@RequestMapping("seller")
public class SLHomeController {
	@Autowired
	private IMilkTeaService iMilkTeaService;

	@Autowired
	private IBranchService iBranhService;

	@Autowired
	private IBranchMilkTeaService iBranchMilkTeaService;

	@Autowired
	private IMilkTeaTypeService iMilkTeaTypeService;
	
	@Autowired
	private IOrderService iOrderService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(MilkTeaType.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				if (text == null || text.isEmpty()) {
					setValue(null);
				} else {
					int id = Integer.parseInt(text);
					Optional<MilkTeaType> milkTeaType = iMilkTeaTypeService.findById(id);
					setValue(milkTeaType.orElse(null));
				}
			}
		});
	}

	@GetMapping("/revenue")
	public String revenue(HttpServletRequest request) {
		// Lấy thông tin user từ session trực tiếp
		HttpSession session = request.getSession(false); // Lấy session nếu có
		User user = (User) session.getAttribute("account"); // Lấy thông tin user từ session

		// Kiểm tra xem user có tồn tại và role của user có phải là admin (roleID == 1)
		if (user != null && user.getRoleID() == 2) {
			return "seller/revenue/revenue";
		} else {
			return "user/error"; // Nếu không phải admin hoặc không có user thì trả về trang lỗi
		}
		
	}

	@GetMapping("/branch")
	public String branch(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
	    User user = (User) session.getAttribute("account");
	    
	    // Kiểm tra nếu BranchID là null
	    if (iBranhService.getBranchID(user.getUserID()) == null) {
	        // Redirect đến endpoint add-branch
	        return "redirect:/seller/add-branch";
	    }
	    
	    // Lấy branch nếu tồn tại
	    Optional<Branch> branchOptional = iBranhService.findById(iBranhService.getBranchID(user.getUserID()));
	    if (branchOptional.isPresent()) {
	        model.addAttribute("branch", branchOptional.get());
	    } else {
	        model.addAttribute("branch", new Branch());
	    }
	    
	    return "seller/branch/branchhome";
	}

	@GetMapping("/milkTeas")
	public String listMilkTea(Model model, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(required = false) Integer typeMilkTeaID, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
	    User user = (User) session.getAttribute("account");
	    
	    // Kiểm tra nếu BranchID là null
	    if (iBranhService.getBranchID(user.getUserID()) == null) {
	        // Redirect đến endpoint add-branch
	        return "redirect:/seller/add-branch";
	    }
	    Optional<Branch> branchOptional = iBranhService.findById(iBranhService.getBranchID(user.getUserID()));
		Branch enBranch = branchOptional.get();
		List<MilkTeaType> listType = iMilkTeaTypeService.findAll();
		Page<BranchMilkTea> brMilkTea;
		brMilkTea = iBranchMilkTeaService.getBranchMilkTeaByBranch(enBranch, pageNo);
		if (brMilkTea == null) {
			return "redirect:/seller/add-MilkTea";
		} 
		
		model.addAttribute("totalPage", brMilkTea.getTotalPages());
		model.addAttribute("listType", listType);
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("milkTeas", brMilkTea);
		return "seller/milkTea/list-MilkTea";
	}

	@GetMapping("/add-branch")
	public String addBranch(Model model) {
		Branch branch = new Branch();
		model.addAttribute("branch", branch);
		return "seller/Branch/add-Branch";
	}
	@GetMapping("/update-branch/{id}")
	public String updateBranch(@PathVariable("id") int theid, Model model) {
	    Optional<Branch> branch = iBranhService.findById(theid);
	    if (branch.isPresent()) {
	        model.addAttribute("branch", branch.get());
	    } else {
	        model.addAttribute("alert", "Không tìm thấy chi nhánh!");
	    }
	    return "seller/Branch/update-Branch";
	}

	@PostMapping("/saveBranch")
	public String saveBranch(@ModelAttribute BranchDto branchDto, Model model, HttpServletRequest request) {
	    String uploadDirectory = PathConstants.UPLOAD_DIRECTORY;
	    List<String> storedFileNames = new ArrayList<>();

	    try {
	        for (MultipartFile image : branchDto.getImages()) {
	            if (!image.isEmpty()) {
	                String originalFileName = image.getOriginalFilename();
	                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();

	                if (!fileExtension.equals(".jpg") && !fileExtension.equals(".png") && !fileExtension.equals(".jpeg")) {
	                    model.addAttribute("alert", "Chỉ chấp nhận ảnh JPG, PNG, JPEG");
	                    return "seller/branch/add-Branch";
	                }

	                String storageFileName = System.currentTimeMillis() + "_" + originalFileName;
	                Path uploadPath = Paths.get(uploadDirectory);
	                if (!Files.exists(uploadPath)) {
	                    Files.createDirectories(uploadPath);
	                }

	                Path filePath = uploadPath.resolve(storageFileName);

	                // Lưu tạm ảnh
	                try (InputStream inputStream = image.getInputStream()) {
	                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
	                }

	                // Kiểm tra MIME type
	                String mimeType = Files.probeContentType(filePath);
	                List<String> allowedMimeTypes = List.of("image/jpeg", "image/png");
	                if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
	                    Files.deleteIfExists(filePath);
	                    model.addAttribute("alert", "Tệp tải lên không phải ảnh hợp lệ.");
	                    return "seller/branch/add-Branch";
	                }

	                // Kiểm tra ảnh thật sự
	                BufferedImage bufferedImage = ImageIO.read(filePath.toFile());
	                if (bufferedImage == null) {
	                    Files.deleteIfExists(filePath);
	                    model.addAttribute("alert", "Tệp tải lên không phải là ảnh.");
	                    return "seller/branch/add-Branch";
	                }

	                // Nếu hợp lệ, thêm vào danh sách
	                storedFileNames.add(storageFileName);
	            }
	        }
	    } catch (Exception ex) {
	        model.addAttribute("alert", "Có lỗi xảy ra khi upload ảnh: " + ex.getMessage());
	        return "seller/branch/add-Branch";
	    }

	    Branch branch;
	    if (branchDto.getBranchID() != null) {
	        Optional<Branch> optionalBranch = iBranhService.findById(branchDto.getBranchID());
	        branch = optionalBranch.orElse(new Branch());
	    } else {
	        branch = new Branch();
	    }

	    branch.setAddress(branchDto.getAddress());
	    branch.setBranchName(branchDto.getBranchName());
	    branch.setOpenTime(branchDto.getOpenTime());
	    branch.setCloseTime(branchDto.getCloseTime());
	    branch.setIntroduction(branchDto.getIntroduction());
	    branch.setDescription(branchDto.getDescription());

	    HttpSession session = request.getSession(false);
	    User user = (User) session.getAttribute("account");
	    branch.setUser(user);

	    branch.setImages(String.join(",", storedFileNames));
	    iBranhService.save(branch);

	    model.addAttribute("success", "Thêm hoặc cập nhật thông tin thành công!");
	    return "redirect:/seller/branch";
	}


	@GetMapping("/add-milkTea")
	public String showAddMilkTeaForm(Model model) {
		List<MilkTeaType> list = iMilkTeaTypeService.findAll();
		model.addAttribute("listType", list);
		model.addAttribute("milkTea", new MilkTea());
		return "seller/milkTea/add-MilkTea";
	}
	
	@GetMapping("/updateMilkTea/{id}")
	public String showUpdateMilkTeaForm(@PathVariable("id") int id, Model model) {
		List<MilkTeaType> list = iMilkTeaTypeService.findAll();
		Optional<MilkTea> milkTea = iMilkTeaService.findById(id);
	    if (milkTea.isPresent()) {
	        model.addAttribute("milkTea", milkTea.get());
	    } else {
	        model.addAttribute("alert", "Không tìm thấy chi nhánh!");
	    }
		model.addAttribute("listType", list);
		return "seller/milkTea/update-MilkTea";
	}

	@PostMapping("/milktea/save")
	public String saveMilkTea(@ModelAttribute MilkTeaDto milkTeaDto, Model model, HttpServletRequest request) {
	    String uploadDirectory = PathConstants.UPLOAD_DIRECTORY;
	    List<String> storedFileNames = new ArrayList<>();

	    try {
	        for (MultipartFile image : milkTeaDto.getImages()) {
	            if (!image.isEmpty()) {
	                String originalFilename = image.getOriginalFilename();
	                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

	                // Tạo tên file lưu trữ duy nhất
	                String storageFileName = System.currentTimeMillis() + "_" + originalFilename;
	                Path uploadPath = Paths.get(uploadDirectory);
	                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

	                Path filePath = uploadPath.resolve(storageFileName);

	                // Lưu file tạm vào hệ thống
	                try (InputStream inputStream = image.getInputStream()) {
	                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
	                }

	                // Bước kiểm tra MIME type
	                String mimeType = Files.probeContentType(filePath);
	                List<String> allowedMimeTypes = List.of("image/jpeg", "image/png");
	                if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
	                    Files.deleteIfExists(filePath);
	                    model.addAttribute("alert", "Tệp tải lên không phải ảnh hợp lệ.");
	                    return "seller/milkTea/add-MilkTea";
	                }

	                // Bước kiểm tra nội dung ảnh thật
	                BufferedImage bufferedImage = ImageIO.read(filePath.toFile());
	                if (bufferedImage == null) {
	                    Files.deleteIfExists(filePath);
	                    model.addAttribute("alert", "Tệp tải lên không phải là ảnh thực sự.");
	                    return "seller/milkTea/add-MilkTea";
	                }

	                // Nếu hợp lệ, thêm vào danh sách lưu DB
	                storedFileNames.add(storageFileName);
	            }
	        }
	    } catch (IOException ex) {
	        model.addAttribute("alert", "Có lỗi xảy ra khi xử lý ảnh: " + ex.getMessage());
	        return "seller/milkTea/add-MilkTea";
	    }

	    // Tạo hoặc cập nhật MilkTea
	    MilkTea milkTea;
	    if (milkTeaDto.getMilkTeaID() != null) {
	        milkTea = iMilkTeaService.findById(milkTeaDto.getMilkTeaID()).orElse(new MilkTea());
	    } else {
	        milkTea = new MilkTea();
	    }

	    milkTea.setMilkTeaName(milkTeaDto.getMilkTeaName());
	    milkTea.setPrice(milkTeaDto.getPrice());
	    milkTea.setDiscountPrice(milkTeaDto.getDiscountPrice());
	    milkTea.setDescription(milkTeaDto.getDescription());
	    milkTea.setIntroduction(milkTeaDto.getIntroduction());
	    milkTea.setMilkTeaType(milkTeaDto.getMilkTeaType());
	    milkTea.setImage(String.join(",", storedFileNames));

	    iMilkTeaService.save(milkTea);
	    model.addAttribute("success", "Thêm trà sữa thành công!");

	    // Gắn vào chi nhánh
	    HttpSession session = request.getSession(false);
	    User user = (User) session.getAttribute("account");
	    Optional<Branch> branchOptional = iBranhService.findById(iBranhService.getBranchID(user.getUserID()));
	    Branch enBranch = branchOptional.get();

	    if (milkTeaDto.getMilkTeaID() == null) {
	        BranchMilkTea branchMilkTea = new BranchMilkTea(enBranch, milkTea, milkTeaDto.getStockQuantity());
	        iBranchMilkTeaService.save(branchMilkTea);
	    } else {
	        BranchMilkTea branchMilkTea = iBranchMilkTeaService.getBranchMilkTea(enBranch, milkTea);
	        branchMilkTea.setStockQuantity(milkTeaDto.getStockQuantity());
	        iBranchMilkTeaService.save(branchMilkTea);
	    }

	    return "redirect:/seller/milkTeas";
	}

	
	@GetMapping("/orders")
	public String listOrder(Model model, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo
			, HttpServletRequest request) {
		Pageable pageable = PageRequest.of(pageNo-1, 2);
		Page<Order> listOrder= iOrderService.findAll(pageable);
		model.addAttribute("totalPage", listOrder.getTotalPages());
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("listOrder", listOrder);
		return "seller/order/list-Order";
	}
	
	@PostMapping("/orders/{id}/update-status")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateOrderStatus(@PathVariable int id) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        Optional<Order> opOrder = iOrderService.findById(id);
	        Order order = opOrder.get();
	        if (order != null && !order.getStatus().equals("COMPLETED")) {
	            order.setStatus(OrderStatus.COMPLETED);
	            iOrderService.save(order);
	            response.put("success", true);
	        } else {
	            response.put("success", false);
	            response.put("message", "Order not found or already completed.");
	        }
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "An error occurred.");
	    }
	    return ResponseEntity.ok(response);
	}

}
