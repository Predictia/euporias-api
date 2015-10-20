package eu.euporias.api.service;

import java.io.Serializable;

import org.springframework.data.crate.repository.CrateRepository;

public interface CrateService<T, R extends CrateRepository<T, ID>, ID extends Serializable> {

	public void refresh();

	public R getRepo();
	
}
