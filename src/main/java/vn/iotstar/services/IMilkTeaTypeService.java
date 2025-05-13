package vn.iotstar.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import vn.iotstar.entity.MilkTeaType;

public interface IMilkTeaTypeService {

	List<MilkTeaType> findAll();
	
	void delete(MilkTeaType entity);

	void deleteById(Integer id);

	long count();

	Optional<MilkTeaType> findById(Integer id);


	Page<MilkTeaType> findAll(Pageable pageable);
	

	List<MilkTeaType> findAll(Sort sort);

	<S extends MilkTeaType> S save(S entity);

}
