package eu.euporias.api.service;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface RepositoryService<T, ID extends Serializable, R extends PagingAndSortingRepository<T, ID>> {

	public R getRepo();
	
}
