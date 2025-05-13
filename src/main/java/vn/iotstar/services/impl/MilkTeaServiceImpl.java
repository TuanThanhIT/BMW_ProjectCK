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

import vn.iotstar.entity.MilkTea;
import vn.iotstar.entity.MilkTeaType;
import vn.iotstar.repository.MilkTeaRepository;
import vn.iotstar.services.IMilkTeaService;


@Service
public class MilkTeaServiceImpl implements IMilkTeaService{
	
	@Autowired
	private MilkTeaRepository milkTeaRepository;
	
	@Override
	public List<MilkTea> findAll() {
		return milkTeaRepository.findAll();
	}

	@Override
	public <S extends MilkTea> S save(S entity) {
		return milkTeaRepository.save(entity);
	}

	@Override
	public List<MilkTea> findAll(Sort sort) {
		return milkTeaRepository.findAll(sort);
	}
	
	@Override
	public Optional<MilkTea> findById(Integer id) {
		return milkTeaRepository.findById(id);
	}

	@Override
	public void delete(MilkTea entity) {
		milkTeaRepository.delete(entity);
	}

	@Override
	public List<MilkTea> findByMilkTeaType_MilkTeaTypeID(int milkTeaTypeID) {
		return milkTeaRepository.findByMilkTeaType_MilkTeaTypeID(milkTeaTypeID);
	}

	

	@Override
	public List<MilkTea> findByMilkTeaTypeAndMilkTeaIDNot(MilkTeaType milkTeaType, int milkTeaID) {
		return milkTeaRepository.findByMilkTeaTypeAndMilkTeaIDNot(milkTeaType, milkTeaID);
	}

	@Override
	public Page<MilkTea> findAll(Integer pageNo) {
		Pageable pageable = PageRequest.of(pageNo-1, 8);
		return this.milkTeaRepository.findAll(pageable);
	}

	@Override
	public Page<MilkTea> findByMilkTeaType_MilkTeaTypeID(int milkTeaTypeID, Integer pageNo) {
		Pageable pageable = PageRequest.of(pageNo-1, 8);
		return this.milkTeaRepository.findByMilkTeaType_MilkTeaTypeID(milkTeaTypeID, pageable);
	}

	@Override
	public List<MilkTea> searchMilkTea(String keyword) {
		return milkTeaRepository.searchMilkTea(keyword);
	}

	@Override
	public Page<MilkTea> searchMilkTea(String keyword, Integer pageNo) {
		List list = this.searchMilkTea(keyword);
		Pageable pageable = PageRequest.of(pageNo-1, 8);
		Integer start = (int)pageable.getOffset();
		Integer end = (int)((pageable.getOffset() + pageable.getPageSize()) > list.size() ? list.size() : pageable.getOffset() + pageable.getPageSize());
		list = list.subList(start, end);
		return new PageImpl<MilkTea>(list, pageable, this.searchMilkTea(keyword).size());
	}

	@Override
	public List<MilkTea> searchMilkTeaByType(int milkTeaTypeID, String keyword) {
		return milkTeaRepository.searchMilkTeaByType(milkTeaTypeID, keyword);
	}
	
	@Override
	public Page<MilkTea> searchMilkTeaByType(String keyword, int milkTeaTypeID, Integer pageNo) {
		List list = this.searchMilkTeaByType(milkTeaTypeID, keyword);
		Pageable pageable = PageRequest.of(pageNo-1, 8);
		Integer start = (int)pageable.getOffset();
		Integer end = (int)((pageable.getOffset() + pageable.getPageSize()) > list.size() ? list.size() : pageable.getOffset() + pageable.getPageSize());
		list = list.subList(start, end);
		return new PageImpl<MilkTea>(list, pageable, this.searchMilkTeaByType(milkTeaTypeID,keyword).size());
	}
	
	
	@Override
	public Page<MilkTea> findAll(Pageable pageable) {
		return milkTeaRepository.findAll(pageable);
	}

	

	
	@Override
	public long count() {
		return milkTeaRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		milkTeaRepository.deleteById(id);
	}

	

	@Override
	public List<MilkTea> findByMilkTeaNameContaining(String keyword) {
		return  milkTeaRepository.findByMilkTeaNameContaining(keyword);
	}

	
	
	
	
	
	
	
}