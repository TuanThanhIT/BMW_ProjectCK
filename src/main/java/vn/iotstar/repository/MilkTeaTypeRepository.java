package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.MilkTeaType;

@Repository
public interface MilkTeaTypeRepository extends JpaRepository<MilkTeaType, Integer>{
	
}
