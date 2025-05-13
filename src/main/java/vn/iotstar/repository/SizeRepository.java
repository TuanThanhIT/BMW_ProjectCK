package vn.iotstar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Sizes;

@Repository
public interface SizeRepository extends JpaRepository<Sizes,Integer>{
	Optional<Sizes> findBysizeName(String size);
}
