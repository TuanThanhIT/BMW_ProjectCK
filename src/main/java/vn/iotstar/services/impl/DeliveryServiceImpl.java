package vn.iotstar.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Delivery;
import vn.iotstar.repository.DeliveryRepository;
import vn.iotstar.repository.OrderRepository;
import vn.iotstar.services.IDeliveryService;

@Service
public class DeliveryServiceImpl implements IDeliveryService {
	@Override
	public Optional<Delivery> findById(Integer id) {
		return deliveryRepository.findById(id);
	}



	@Override
	public <S extends Delivery> S save(S entity) {
		return deliveryRepository.save(entity);
	}

	@Override
	public List<Delivery> findAll(Sort sort) {
		return deliveryRepository.findAll(sort);
	}

	@Override
	public Page<Delivery> findAll(Pageable pageable) {
		return deliveryRepository.findAll(pageable);
	}

	@Override
	public List<Delivery> findAll() {
		return deliveryRepository.findAll();
	}

	@Override
	public <S extends Delivery> boolean exists(Example<S> example) {
		return deliveryRepository.exists(example);
	}

	@Override
	public long count() {
		return deliveryRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		deliveryRepository.deleteById(id);
	}

	

	@Override
	public void delete(Delivery entity) {
		deliveryRepository.delete(entity);
	}

	@Autowired
	DeliveryRepository deliveryRepository;

	public DeliveryServiceImpl(DeliveryRepository deliveryRepository) {
		super();
		this.deliveryRepository = deliveryRepository;
	}
	
	

}
