package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Branch;
import vn.iotstar.entity.BranchMilkTea;
import vn.iotstar.entity.MilkTea;

@Repository
public interface BranchMilkTeaRepository extends JpaRepository<BranchMilkTea, Integer> {
	@Query("SELECT bm FROM BranchMilkTea bm WHERE bm.branch = :branch")
	Page<BranchMilkTea> findByBranch(@Param("branch") Branch branch, Pageable pageable);

	@Query("SELECT bmt FROM BranchMilkTea bmt WHERE bmt.branch = :branch AND bmt.milkTea = :milkTea")
	BranchMilkTea findBranchMilkTeaByBranchAndMilkTea(@Param("branch") Branch branch,
			@Param("milkTea") MilkTea milkTea);

	@Query("SELECT b.sellQuantity FROM BranchMilkTea b WHERE b.milkTea.milkTeaID = :milkTeaID")
	Integer findSellQuantityByMilkTeaID(@Param("milkTeaID") int milkTeaID);

	@Query("SELECT b.milkTea FROM BranchMilkTea b ORDER BY b.sellQuantity DESC")
	List<MilkTea> findTop3BySellQuantity(Pageable pageable);

}
