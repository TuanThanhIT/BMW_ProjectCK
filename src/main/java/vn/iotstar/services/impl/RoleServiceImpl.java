package vn.iotstar.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Role;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.services.IRoleService;

@Service
public class RoleServiceImpl implements IRoleService{
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public List<Role> findByRoleNameNot(String roleName) {
		return roleRepository.findByRoleNameNot(roleName);
	}
	
	
	
	
	
	
}
