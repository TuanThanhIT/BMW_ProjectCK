package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.iotstar.entity.Income;

public interface IIncomeRepository extends JpaRepository<Income, Integer>{
	List<Income> findByBranch_BranchID(int branchId);
	@Query("SELECT SUM(i.value) FROM Income i")
    double sumIncomeValue();
	
	// doanh thu từng tháng
	@Query("SELECT MONTH(i.date) AS month, SUM(i.value) AS totalIncome " +
            "FROM Income i WHERE YEAR(i.date) = :year " +
            "GROUP BY MONTH(i.date) ORDER BY month")
    List<Object[]> findMonthlyIncome(int year);
    
    // Truy vấn tính tổng thu nhập cho mỗi branch
    @Query("SELECT i.branch.branchID, SUM(i.value) FROM Income i GROUP BY i.branch.branchID")
    List<Object[]> findTotalIncomeByBranch();
    
 // Truy vấn lấy ra 4 chi nhánh có tổng giá trị cao nhất
    @Query("SELECT i.branch, SUM(i.value) AS totalValue " +
           "FROM Income i " +
           "GROUP BY i.branch " +
           "ORDER BY totalValue DESC")
    List<Object[]> findTop4BranchesByTotalValue(Pageable pageable);
}
