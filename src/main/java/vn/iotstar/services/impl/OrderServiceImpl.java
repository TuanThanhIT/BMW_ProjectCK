package vn.iotstar.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Order;
import vn.iotstar.repository.OrderRepository;
import vn.iotstar.services.IOrderService;

@Service
public class OrderServiceImpl implements IOrderService  {

	
	@Autowired
	OrderRepository orderRepository;

	public OrderServiceImpl(OrderRepository orderRepository) {
		super();
		this.orderRepository = orderRepository;
	}
	@Override
	public <S extends Order> S save(S entity) {
		return orderRepository.save(entity);
	}

	@Override
	public List<Order> findAll(Sort sort) {
		return orderRepository.findAll(sort);
	}

	@Override
	public Page<Order> findAll(Pageable pageable) {
		return orderRepository.findAll(pageable);
	}

	@Override
	public List<Order> findAll() {
		return orderRepository.findAll();
	}

	@Override
	public Optional<Order> findById(Integer id) {
		return orderRepository.findById(id);
	}

	@Override
	public long count() {
		return orderRepository.count();
	}

	@Override
	public void delete(Order entity) {
		orderRepository.delete(entity);
	}
	
	@Override
	public List<Order> findByUserId(int userId) {
        return orderRepository.findByUserUserID(userId);
    }

	
	
}
