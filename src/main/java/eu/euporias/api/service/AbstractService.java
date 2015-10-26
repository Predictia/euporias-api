package eu.euporias.api.service;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;

public class AbstractService<T, ID extends Serializable, R extends PagingAndSortingRepository<T, ID>> {

	public AbstractService(Class<T> clazz, R repo) {
		super();
		this.repo = repo;
	}
	
	private final R repo;
	
	public R getRepo() {
		return repo;
	}

	
}
