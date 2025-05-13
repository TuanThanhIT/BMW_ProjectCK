package vn.iotstar.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Shipper;
import vn.iotstar.repository.ShipperRepository;
import vn.iotstar.services.IShipperService;

@Service
public class ShipperServiceImpl implements IShipperService {
	@Override
	public <S extends Shipper> S save(S entity) {
		return shipperRepository.save(entity);
	}

	@Override
	public List<Shipper> findAll(Sort sort) {
		return shipperRepository.findAll(sort);
	}

	@Override
	public Page<Shipper> findAll(Pageable pageable) {
		return shipperRepository.findAll(pageable);
	}

	@Override
	public List<Shipper> findAll() {
		return shipperRepository.findAll();
	}

	@Override
	public Optional<Shipper> findById(Integer id) {
		return shipperRepository.findById(id);
	}

	@Override
	public <S extends Shipper> boolean exists(Example<S> example) {
		return shipperRepository.exists(example);
	}

	@Override
	public long count() {
		return shipperRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		shipperRepository.deleteById(id);
	}

	@Override
	public void delete(Shipper entity) {
		shipperRepository.delete(entity);
	}

	@Autowired
	ShipperRepository shipperRepository;

	public ShipperServiceImpl(ShipperRepository shipperRepository) {
		super();
		this.shipperRepository = shipperRepository;
	}
	
	
}
