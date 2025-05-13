package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Pays;

@Repository
public interface IPaysRepository extends JpaRepository<Pays, Integer>{

}