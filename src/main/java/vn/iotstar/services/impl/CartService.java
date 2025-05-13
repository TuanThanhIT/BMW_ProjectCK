package vn.iotstar.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Branch;
import vn.iotstar.entity.BranchMilkTea;
import vn.iotstar.entity.Cart;
import vn.iotstar.entity.CartMilkTea;
import vn.iotstar.entity.MilkTea;
import vn.iotstar.entity.User;
import vn.iotstar.repository.BranchMilkTeaRepository;
import vn.iotstar.repository.CartMilkTeaRepository;
import vn.iotstar.repository.CartRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.ICartService;

@Service
public class CartService implements ICartService{
	@Autowired
	private CartRepository cartRepo;
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private BranchMilkTeaRepository brMilkTeaRepo;
	@Autowired
	private CartMilkTeaRepository cartMilkTRepo;
	
	@Override
	public Optional<Cart> findByUserId(int id) {
		return cartRepo.findByUserUserID(id);
	}
	
	@Override
	public <S extends Cart> S save(S entity)
	{
		return cartRepo.save(entity);
	}

	@Override
	public Long getTotalProductCount() {
		return cartRepo.getTotalProductCount();
	}	
	@Override
	public void deleteAllItem(int uid) {
		Cart cart = this.findByUserId(uid).get();

		for (CartMilkTea item : cart.getMilkTeas()) {
			MilkTea milkTea = item.getMilkTea(); // Lấy sản phẩm
			int quantityToReduce = item.getQuantityMilkTea(); // Số lượng mua từ giỏ hàng
			List<BranchMilkTea> bMilkTea = milkTea.getBranchMilkTeas();
			for(BranchMilkTea bItem : bMilkTea)
			{
				Branch branch = bItem.getBranch();
				int sellQuantity = bItem.getSellQuantity() + quantityToReduce;
				int stock = bItem.getStockQuantity() - quantityToReduce;
				bItem.setSellQuantity(sellQuantity);
				bItem.setStockQuantity(stock);
				brMilkTeaRepo.save(bItem);
			
			}
			item.setQuantityMilkTea(0);
			cart.setTotalCost(new BigDecimal("0.00"));
			cartMilkTRepo.save(item);
			 
	        
		}
	}
	@Override
	public Optional<Cart> findByUserId1(int id)
	{
		return cartRepo.findByUserId(id);
	}
}	
