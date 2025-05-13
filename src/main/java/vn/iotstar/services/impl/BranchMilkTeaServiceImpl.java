package vn.iotstar.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.iotstar.entity.Branch;
import vn.iotstar.entity.BranchMilkTea;
import vn.iotstar.entity.MilkTea;
import vn.iotstar.services.IBranchMilkTeaService;

@Service
@Transactional
public class BranchMilkTeaServiceImpl implements IBranchMilkTeaService {
	@Autowired
	private vn.iotstar.repository.BranchMilkTeaRepository iBranchMilkTeaRepository;

	@Override
	public List<BranchMilkTea> findAll() {
		return iBranchMilkTeaRepository.findAll();
	}

	@Override
	public Optional<BranchMilkTea> findById(int id) {
		return iBranchMilkTeaRepository.findById(id);
	}

	@Override
	public BranchMilkTea save(BranchMilkTea branchmilkTea) {
		return iBranchMilkTeaRepository.save(branchmilkTea);
	}

	@Override
	public void deleteById(int id) {
		iBranchMilkTeaRepository.deleteById(id);

	}

	@Override
	public Page<BranchMilkTea> getBranchMilkTeaByBranch(Branch branch, int pageNo) {
		Pageable pageable = PageRequest.of(pageNo - 1, 3);
		return iBranchMilkTeaRepository.findByBranch(branch, pageable);
	}

	@Override
	public BranchMilkTea getBranchMilkTea(Branch branch, MilkTea milkTea) {
		return iBranchMilkTeaRepository.findBranchMilkTeaByBranchAndMilkTea(branch, milkTea);
	}

	@Override
	public Integer findSellQuantityByMilkTeaID(int milkTeaID) {
		return iBranchMilkTeaRepository.findSellQuantityByMilkTeaID(milkTeaID);
	}
	
	@Override
	public List<MilkTea> getTop3MilkTea() {
	    Pageable topThree = PageRequest.of(0, 4);
	    return iBranchMilkTeaRepository.findTop3BySellQuantity(topThree);
	}

}
