package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.RememberMeToken;

@Repository
public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, Long> {
    RememberMeToken findByUsername(String username);
    // Truy vấn token theo giá trị token
    RememberMeToken findByToken(String token);
}
