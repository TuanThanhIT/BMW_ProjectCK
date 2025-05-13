package vn.iotstar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.entity.Like;
import vn.iotstar.entity.MilkTea;
import vn.iotstar.entity.User;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
	@Transactional
	void deleteByUserAndMilkTea(User user, MilkTea milkTea);
	
	List<Like> findByUser(User user);
	
	boolean existsByUserAndMilkTea(User user, MilkTea milkTea);
	
	List<MilkTea> findLikedMilkTeasByUser(User user);
	
	Optional<Like> findByUserAndMilkTea(User user, MilkTea milkTea);
	
	List<Like> findByUser_UserID(int userID);
}