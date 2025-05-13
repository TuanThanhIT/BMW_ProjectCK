package vn.iotstar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart,Integer>{
	Optional<Cart> findByUserUserID(int userID);
	@Query("SELECT SUM(c.sellQuantity) FROM BranchMilkTea c")
    Long getTotalProductCount();
	
	@Query("SELECT c FROM Cart c LEFT JOIN FETCH c.milkTeas WHERE c.user.userID = :userId")
	Optional<Cart> findByUserId(@Param("userId") int userId);
}
