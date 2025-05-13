package vn.iotstar.services;

import java.util.List;


import vn.iotstar.entity.Role;

public interface IRoleService {

	List<Role> findByRoleNameNot(String roleName);

	

}
