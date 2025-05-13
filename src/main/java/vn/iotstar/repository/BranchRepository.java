package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Branch;
import vn.iotstar.entity.MilkTea;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {

	@Query("SELECT bm.milkTea FROM BranchMilkTea bm WHERE bm.branch.branchID = :branchID")
	List<MilkTea> findMilkTeaByBranchID(@Param("branchID") int branchID);

	// Tìm Branch theo address
	List<Branch> findByAddressContaining(String cityName);

	Page<Branch> findByAddressContaining(String cityName, Pageable page);

	@Query("SELECT c FROM Branch c WHERE c.branchName LIKE %:keyword%")
	List<Branch> searchBranch(@Param("keyword") String keyword);

	// Tìm kiếm chi nhánh theo từ khóa và cityId
	@Query("SELECT b FROM Branch b WHERE b.branchName LIKE %:keyword% AND b.address LIKE %:cityName%")
	List<Branch> searchBranchInCity(@Param("keyword") String keyword, @Param("cityName") String cityName);

	// Truy vấn trả về branchID dựa trên userID
	@Query("SELECT b.branchID FROM Branch b WHERE b.user.userID = :userID")
	Integer findBranchIDByUserID(@Param("userID") int userID);

}