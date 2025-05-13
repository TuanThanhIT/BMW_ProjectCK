package vn.iotstar.services;

import java.util.List;

import vn.iotstar.entity.MilkTea;
import vn.iotstar.entity.Rate;

public interface IRateService {

	<S extends Rate> S save(S entity);
	List<Rate> findAll();
	List<Rate> findByMilkTea(MilkTea milkTea);
	

}