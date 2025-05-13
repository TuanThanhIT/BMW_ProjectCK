package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Pays;
import vn.iotstar.repository.IPaysRepository;
import vn.iotstar.services.IPayService;

@Service
public class PayServiceImpl implements IPayService{
	@Autowired
	private IPaysRepository payRepo;
	@Override
	public <S extends Pays> S save(S entity) {
		return payRepo.save(entity);
	}
}