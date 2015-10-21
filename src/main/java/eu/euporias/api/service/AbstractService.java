package eu.euporias.api.service;

import org.springframework.data.orient.object.repository.OrientObjectRepository;

public class AbstractService<T, R extends OrientObjectRepository<T>> {

	public AbstractService(Class<T> clazz, R repo) {
		super();
		this.repo = repo;
	}
	
	private final R repo;
	
	public R getRepo() {
		return repo;
	}

	
}
