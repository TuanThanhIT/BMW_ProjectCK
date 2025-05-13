package vn.iotstar.services;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import vn.iotstar.entity.CartMilkTea;
import vn.iotstar.repository.CartMilkTeaRepository;

public interface ICartMilkTeaService {


	BigDecimal calculateTotalPrice(int cartId);

	Optional<CartMilkTea> findById(int id);

	void deleteById(Integer id);

	void delete(CartMilkTea entity);

	void deleteByIdCustom(int id);

	
	

}
