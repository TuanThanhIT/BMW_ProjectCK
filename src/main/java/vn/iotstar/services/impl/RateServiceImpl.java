package vn.iotstar.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.MilkTea;
import vn.iotstar.entity.Rate;
import vn.iotstar.repository.RateRepository;
import vn.iotstar.services.IRateService;

@Service
public class RateServiceImpl implements IRateService{
	
	@Autowired
	private RateRepository rateRepository;

	
	@Override
	public List<Rate> findAll() {
		return rateRepository.findAll();
	}


	@Override
	public <S extends Rate> S save(S entity) {
		return rateRepository.save(entity);
	}


	@Override
	public List<Rate> findByMilkTea(MilkTea milkTea) {
		return rateRepository.findByMilkTea(milkTea);
	}
	
	
	
	
}