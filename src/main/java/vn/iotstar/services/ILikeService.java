package vn.iotstar.services;

import java.util.List;
import java.util.Optional;

import vn.iotstar.entity.Like;
import vn.iotstar.entity.MilkTea;
import vn.iotstar.entity.User;

public interface ILikeService {

	List<Like> findAll();

	<S extends Like> S save(S entity);


	void deleteByUserAndMilkTea(User user, MilkTea milkTea);

	List<Like> findByUser(User user);

	boolean existsByUserAndMilkTea(User user, MilkTea milkTea);

	List<MilkTea> findLikedMilkTeasByUser(User user);

	Optional<Like> findByUserAndMilkTea(User user, MilkTea milkTea);


	List<MilkTea> getLikedMilkTeas(int userID);

}