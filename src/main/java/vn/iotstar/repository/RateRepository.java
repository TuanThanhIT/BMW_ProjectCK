package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.MilkTea;
import vn.iotstar.entity.Rate;

@Repository
public interface RateRepository extends JpaRepository<Rate, Integer>{
	
	List<Rate> findByMilkTea(MilkTea milkTea);
}