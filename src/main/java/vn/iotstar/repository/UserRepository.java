package vn.iotstar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.User;


@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	Optional<User> findByUserName(String userName);
    boolean existsByUserName(String username);
    List<User> findByRoleID(int roleID);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleName = :role")
    long countByRole(String role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleName = 'SHIPPER'")
    long countShipperRole();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleName = 'SELLER'")
    long countSellerRole();
    boolean existsByEmail(String email);
    User findByEmail(String email);

}
