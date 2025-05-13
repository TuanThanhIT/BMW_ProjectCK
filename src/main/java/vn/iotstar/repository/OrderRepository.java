package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

	List<Order> findByUserUserID(int userId);
}
