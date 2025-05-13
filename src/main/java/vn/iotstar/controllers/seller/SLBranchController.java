package vn.iotstar.controllers.seller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.iotstar.entity.Branch;
import vn.iotstar.services.IBranchService;


@RestController
@RequestMapping("/sellers")
public class SLBranchController {
	@Autowired
	private IBranchService iBranchService;
	
	@GetMapping("/branchs")
	public List<Branch>getBranchs(){
		return iBranchService.findAll();
	}
	
	@GetMapping("/branchs/{branchId}")
	public Optional<Branch> getBranch(@PathVariable int branchId) {
		return iBranchService.findById(branchId);
	}
	
	@PostMapping("/branchs")
	public Branch addBranch(@RequestBody Branch branch) {
		branch.setBranchID(0);
		Branch dbBranch = iBranchService.save(branch);
		return dbBranch;
	}
	
	@PutMapping("/branchs")
	public Branch updateBranch(@RequestBody Branch branch) {
		Branch dbBranch = iBranchService.save(branch);
		return dbBranch;
	}
	
	@DeleteMapping("/branchs/{branchId}")
	public String deleteBranch(@PathVariable int branchId)
	{
		Optional<Branch> tempBranch = iBranchService.findById(branchId);
		if(tempBranch==null) {
			throw new RuntimeException("Id chi nhánh không tồn tại: "+branchId);
			
		}
		else {
			iBranchService.deleteById(branchId);
		}
		
		return "Chi nhánh có ID: " +branchId+" đã được xóa";
	}
	
}
