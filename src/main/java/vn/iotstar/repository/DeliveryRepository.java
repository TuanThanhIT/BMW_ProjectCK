package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Delivery;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {

}
