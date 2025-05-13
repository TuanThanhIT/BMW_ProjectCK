package vn.iotstar.services;

import java.util.Optional;

import org.springframework.data.repository.query.Param;

import vn.iotstar.entity.Cart;

public interface ICartService {

	Optional<Cart> findByUserId(int id);

	<S extends Cart> S save(S entity);
    Long getTotalProductCount();

	void deleteAllItem(int uid);

	Optional<Cart> findByUserId1(int id);



}
