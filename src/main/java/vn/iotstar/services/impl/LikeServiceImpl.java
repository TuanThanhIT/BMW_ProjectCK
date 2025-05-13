package vn.iotstar.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.entity.Like;
import vn.iotstar.entity.MilkTea;
import vn.iotstar.entity.User;
import vn.iotstar.repository.LikeRepository;
import vn.iotstar.services.ILikeService;

@Service
public class LikeServiceImpl implements ILikeService{
	
	@Autowired
	private LikeRepository likeRepository;


	
	@Override
	@Transactional
	public void deleteByUserAndMilkTea(User user, MilkTea milkTea) {
		likeRepository.deleteByUserAndMilkTea(user, milkTea);
	}

	@Override
	public <S extends Like> S save(S entity) {
		return likeRepository.save(entity);
	}

	@Override
	public List<Like> findAll() {
		return likeRepository.findAll();
	}

	@Override
	public List<Like> findByUser(User user) {
		return likeRepository.findByUser(user);
	}

	@Override
	public boolean existsByUserAndMilkTea(User user, MilkTea milkTea) {
		return likeRepository.existsByUserAndMilkTea(user, milkTea);
	}

	@Override
	public List<MilkTea> findLikedMilkTeasByUser(User user) {
		return likeRepository.findLikedMilkTeasByUser(user);
	}

	@Override
	public Optional<Like> findByUserAndMilkTea(User user, MilkTea milkTea) {
		return likeRepository.findByUserAndMilkTea(user, milkTea);
	}

	@Override
	public List<MilkTea> getLikedMilkTeas(int userID) {
        // Lấy tất cả các Like của người dùng
        List<Like> likes = likeRepository.findByUser_UserID(userID);

        // Trả về danh sách MilkTea mà người dùng đã thích
        return likes.stream()
                    .map(Like::getMilkTea)  // Lấy MilkTea từ mỗi Like
                    .collect(Collectors.toList());
    }
	
	
	
	
	
	
	

}