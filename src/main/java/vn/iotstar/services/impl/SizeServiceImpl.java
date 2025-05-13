package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.MilkTeaType;
import vn.iotstar.entity.Sizes;
import vn.iotstar.repository.SizeRepository;
import vn.iotstar.services.ISizeService;

@Service
public class SizeServiceImpl implements ISizeService{
	@Autowired
	private SizeRepository sizerepo;
	
	@Override
	public Sizes findByName(String name) {
        return sizerepo.findBysizeName(name)
                .orElseThrow(() -> new RuntimeException("MilkTeaType không tồn tại với kích cỡ: " + name));
    }
}
