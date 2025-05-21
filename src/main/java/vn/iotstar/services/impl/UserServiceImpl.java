package vn.iotstar.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.IUserService;

@Service
public class UserServiceImpl implements IUserService, UserDetailsService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public List<User> findAll(Sort sort) {
		return userRepository.findAll(sort);
	}

	public Page<User> findAll(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	public long count() {
		return userRepository.count();
	}

	public void deleteById(Integer id) {
		userRepository.deleteById(id);
	}

	public void delete(User entity) {
		userRepository.delete(entity);
	}

	public void deleteAll() {
		userRepository.deleteAll();
	}


//	@Override
//	public User login(String username, String password) {
//        Optional<User> userOptional = userRepository.findByUserName(username);
//        
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            // Sử dụng passwordEncoder để kiểm tra mật khẩu đã mã hóa
//            if (passwordEncoder.matches(password, user.getPassword())) {
//                return user; // Nếu mật khẩu hợp lệ, trả về user
//            }
//        }
//        
//        return null;  // Nếu không tìm thấy user hoặc mật khẩu không hợp lệ
//    }
	
	@Override
	public User getUserByUsername(String username) {
		return userRepository.findByUserName(username).orElse(null); // Trả về null nếu không tìm thấy người dùng
	}

	@Override
	public User addUser(User user) {
		return userRepository.save(user); // Thêm mới
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public Optional<User> findById(Integer id) {
		return userRepository.findById(id);
	}

	@Override
	@Cacheable(value = "usernameExists", key = "#username")
	public boolean existsByUserName(String username) {
		return userRepository.existsByUserName(username);
	}

	@Override
	public <S extends User> S save(S entity) {
		return userRepository.save(entity);
	}
	
	@Override
    @Cacheable(value = "emailExists", key = "#email")
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}
	
	
	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	

	@Override
	public long countByRole(String role) {
		return userRepository.countByRole(role);
	}

	@Override
	public long countShipperRole() {
		return userRepository.countShipperRole();
	}

	@Override
	public long countSellerRole() {
		return userRepository.countSellerRole();
	}

	@Override
	public List<User> findByRoleID(int roleID) {
		return userRepository.findByRoleID(roleID);
	}
	
	@Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user;
    }
	
}

