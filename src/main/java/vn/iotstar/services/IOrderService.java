package vn.iotstar.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import vn.iotstar.entity.Order;

public interface IOrderService {

	void delete(Order entity);

	long count();

	Optional<Order> findById(Integer id);

	List<Order> findAll();

	Page<Order> findAll(Pageable pageable);

	List<Order> findAll(Sort sort);

	<S extends Order> S save(S entity);

	List<Order> findByUserId(int userId);

}
