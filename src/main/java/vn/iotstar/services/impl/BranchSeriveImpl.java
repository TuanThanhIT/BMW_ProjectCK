package vn.iotstar.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Branch;
import vn.iotstar.entity.MilkTea;
import vn.iotstar.repository.BranchRepository;
import vn.iotstar.services.IBranchService;

@Service
public class BranchSeriveImpl implements IBranchService {

	@Autowired
	BranchRepository branchRepository;

	@Override
	public <S extends Branch> S save(S entity) {
		return branchRepository.save(entity);
	}

	@Override
	public List<Branch> findAll(Sort sort) {
		return branchRepository.findAll(sort);
	}

	@Override
	public List<Branch> findAll() {
		return branchRepository.findAll();
	}

	@Override
	public Optional<Branch> findById(Integer id) {
		return branchRepository.findById(id);
	}

	@Override
	public long count() {
		return branchRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		branchRepository.deleteById(id);
	}

	@Override
	public void delete(Branch entity) {
		branchRepository.delete(entity);
	}

	@Override
	public void deleteAll() {
		branchRepository.deleteAll();
	}

	@Override
	public List<MilkTea> findMilkTeaByBranchID(int branchID) {
		return branchRepository.findMilkTeaByBranchID(branchID);
	}

	@Override
	public List<Branch> findByAddressContaining(String cityName) {
		return branchRepository.findByAddressContaining(cityName);
	}

	@Override
	public Page<Branch> findByAddressContaining(String cityName, Integer pageNo) {
		Pageable pageable = PageRequest.of(pageNo - 1, 2);
		return branchRepository.findByAddressContaining(cityName, pageable);
	}

	@Override
	public Page<Branch> findAll(Integer pageNo) {
		Pageable pageable = PageRequest.of(pageNo - 1, 2);
		return branchRepository.findAll(pageable);
	}

	@Override
	public List<Branch> searchBranch(String keyword) {
		return branchRepository.searchBranch(keyword);
	}

	@Override
	public Page<Branch> searchBranch(String keyword, Integer pageNo) {
		List list = this.searchBranch(keyword);
		Pageable pageable = PageRequest.of(pageNo - 1, 2);
		Integer start = (int) pageable.getOffset();
		Integer end = (int) ((pageable.getOffset() + pageable.getPageSize()) > list.size() ? list.size()
				: pageable.getOffset() + pageable.getPageSize());
		list = list.subList(start, end);
		return new PageImpl<Branch>(list, pageable, this.searchBranch(keyword).size());
	}

	// Tìm kiếm chi nhánh theo keyword và cityId với phân trang
	@Override
	public Page<Branch> searchBranchInCity(String keyword, String cityName, Integer pageNo) {
		// Gọi phương thức tìm kiếm từ Repository
		List<Branch> list = branchRepository.searchBranchInCity(keyword, cityName);

		// Tạo Pageable để phân trang
		Pageable pageable = PageRequest.of(pageNo - 1, 2); // Giới hạn số chi nhánh trên mỗi trang là 2
		Integer start = (int) pageable.getOffset();
		Integer end = (int) ((pageable.getOffset() + pageable.getPageSize()) > list.size() ? list.size()
				: pageable.getOffset() + pageable.getPageSize());

		list = list.subList(start, end);

		// Trả về kết quả phân trang
		return new PageImpl<>(list, pageable, branchRepository.searchBranchInCity(keyword, cityName).size());
	}

	@Override
	public Integer getBranchID(int userid) {
		return branchRepository.findBranchIDByUserID(userid);
	}

}
