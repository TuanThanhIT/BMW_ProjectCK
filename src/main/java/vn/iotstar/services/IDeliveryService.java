package vn.iotstar.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import vn.iotstar.entity.Delivery;

public interface IDeliveryService {

	void delete(Delivery entity);

	void deleteById(Integer id);

	long count();

	<S extends Delivery> boolean exists(Example<S> example);

	List<Delivery> findAll();

	Page<Delivery> findAll(Pageable pageable);

	List<Delivery> findAll(Sort sort);

	<S extends Delivery> S save(S entity);

	Optional<Delivery> findById(Integer id);

}
