package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.MilkTea;
import vn.iotstar.entity.MilkTeaType;

@Repository
public interface MilkTeaRepository extends JpaRepository<MilkTea, Integer> {
	List<MilkTea> findByMilkTeaType_MilkTeaTypeID(int milkTeaTypeID);

	Page<MilkTea> findByMilkTeaType_MilkTeaTypeID(int milkTeaTypeID, Pageable pageable);

	List<MilkTea> findByMilkTeaTypeAndMilkTeaIDNot(MilkTeaType milkTeaType, int milkTeaID);

	@Query("SELECT c FROM MilkTea c WHERE c.milkTeaName LIKE %:keyword%")
	List<MilkTea> searchMilkTea(@Param("keyword") String keyword);
	
	@Query("SELECT m FROM MilkTea m WHERE m.milkTeaType.milkTeaTypeID = :milkTeaTypeID " +
	           "AND m.milkTeaName LIKE %:keyword%")
	List<MilkTea> searchMilkTeaByType(@Param("milkTeaTypeID") int milkTeaTypeID, 
	                                              @Param("keyword") String keyword);
	
	@Query("SELECT mt FROM MilkTea mt WHERE mt.milkTeaName LIKE %:keyword%")
	List<MilkTea> findByMilkTeaNameContaining(@Param("keyword") String keyword);
}