package vn.iotstar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.entity.CartMilkTea;

@Repository
public interface CartMilkTeaRepository extends JpaRepository<CartMilkTea, Integer> {
	List<CartMilkTea> findByCartCartID(int cartID);

	Optional<CartMilkTea> findById(int id);

	@Modifying
	@Transactional
	@Query("DELETE FROM CartMilkTea c WHERE c.id = :id")
	void deleteByIdCustom(@Param("id") int id);
}
