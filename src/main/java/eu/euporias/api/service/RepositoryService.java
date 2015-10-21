package eu.euporias.api.service;

import org.springframework.data.orient.object.repository.OrientObjectRepository;

public interface RepositoryService<T, R extends OrientObjectRepository<T>> {

	public R getRepo();
	
}
