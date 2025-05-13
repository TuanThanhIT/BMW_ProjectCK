package vn.iotstar.services;

import vn.iotstar.entity.Sizes;

public interface ISizeService {

	Sizes findByName(String name);

}
