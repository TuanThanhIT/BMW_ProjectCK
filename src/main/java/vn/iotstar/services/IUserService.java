package vn.iotstar.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.User;
import vn.iotstar.model.UserDto;

public interface IUserService {

	Optional<User> findById(Integer id);

	List<User> findAll();

	User addUser(User user);

	User login(String username, String password);

	//boolean checkUserNameExists(String username);
	
	//boolean register(String fullName, String address, String phone, String email, String userName, String password, String image, int roleID);

	void delete(User entity);

	long count();

	Page<User> findAll(Pageable pageable);

	<S extends User> S save(S entity);


	boolean existsByUserName(String username);

	User getUserByUsername(String username);

    long countByRole(String role);
    long countShipperRole();
    long countSellerRole();
    List<User> findByRoleID(int roleID);
	boolean existsByEmail(String email);

	User findByEmail(String email);



}
