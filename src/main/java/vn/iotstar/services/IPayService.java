package vn.iotstar.services;

import vn.iotstar.entity.Pays;

public interface IPayService {

	<S extends Pays> S save(S entity);

}
