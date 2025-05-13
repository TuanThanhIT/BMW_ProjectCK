package vn.iotstar.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import vn.iotstar.entity.Shipper;

public interface IShipperService {

	void delete(Shipper entity);

	void deleteById(Integer id);

	long count();

	<S extends Shipper> boolean exists(Example<S> example);

	Optional<Shipper> findById(Integer id);

	List<Shipper> findAll();

	Page<Shipper> findAll(Pageable pageable);

	List<Shipper> findAll(Sort sort);

	<S extends Shipper> S save(S entity);

}
