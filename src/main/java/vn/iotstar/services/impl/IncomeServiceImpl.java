package vn.iotstar.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Branch;
import vn.iotstar.repository.IIncomeRepository;
import vn.iotstar.services.IIncomeService;

@Service
public class IncomeServiceImpl implements IIncomeService {
	@Autowired
	IIncomeRepository iIncomeRepository;

	public IncomeServiceImpl(IIncomeRepository iIncomeRepository) {
		this.iIncomeRepository = iIncomeRepository;
	}

	@Override
	public double sumIncomeValue() {
		return iIncomeRepository.sumIncomeValue();
	}

	@Override
	public List<Object[]> findMonthlyIncome(int year) {
		return iIncomeRepository.findMonthlyIncome(year);
	}

	@Override
	public List<Object[]> findTotalIncomeByBranch() {
		return iIncomeRepository.findTotalIncomeByBranch();
	}
	
	// Phương thức lấy ra các chi nhánh có tổng giá trị cao nhất
    @Override
	public List<Branch> getTop4BranchesByTotalValue() {
        Pageable pageable = PageRequest.of(0, 4); // Giới hạn 4 kết quả
        List<Object[]> result = iIncomeRepository.findTop4BranchesByTotalValue(pageable);
        
        // Chuyển đổi kết quả trả về từ query thành danh sách các đối tượng Branch
        return result.stream()
                     .map(row -> (Branch) row[0]) // row[0] chứa đối tượng Branch
                     .toList();
    }

}
