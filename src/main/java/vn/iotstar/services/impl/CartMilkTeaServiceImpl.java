package vn.iotstar.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.CartMilkTea;
import vn.iotstar.repository.CartMilkTeaRepository;
import vn.iotstar.services.ICartMilkTeaService;

@Service
public class CartMilkTeaServiceImpl implements ICartMilkTeaService {

	

	@Override
	public void deleteByIdCustom(int id) {
		cartMilkTeaRepository.deleteByIdCustom(id);
	}

	@Autowired
	private CartMilkTeaRepository cartMilkTeaRepository;

	@Override
	public BigDecimal calculateTotalPrice(int cartId) {
		List<CartMilkTea> cartMilkTeas = cartMilkTeaRepository.findByCartCartID(cartId);
		BigDecimal totalPrice = BigDecimal.ZERO; // Khởi tạo giá trị mặc định

		for (CartMilkTea cartMilkTea : cartMilkTeas) {
			if (cartMilkTea.getMilkTea() != null && cartMilkTea.getMilkTea().getPrice() != null) {
				BigDecimal basePrice = cartMilkTea.getMilkTea().getPrice(); // Giá cơ bản
				BigDecimal sizeExtraPrice = cartMilkTea.getSize() != null
						&& cartMilkTea.getSize().getExtraCost() != null ? cartMilkTea.getSize().getExtraCost()
								: BigDecimal.ZERO; // Giá cộng thêm của size
				BigDecimal itemPrice = basePrice.add(sizeExtraPrice); // Giá cho một sản phẩm (bao gồm size)
				totalPrice = totalPrice.add(itemPrice.multiply(BigDecimal.valueOf(cartMilkTea.getQuantityMilkTea()))); // Tổng
																														// giá
			}
		}
	return totalPrice;
	}
	@Override
	public Optional<CartMilkTea> findById(int id)
	{
		return cartMilkTeaRepository.findById(id);
	}
	
	@Override
	public void deleteById(Integer id) {
		cartMilkTeaRepository.deleteById(id);
	}
	
	@Override
	public void delete(CartMilkTea entity) {
		cartMilkTeaRepository.delete(entity);
	}
	

	
}
