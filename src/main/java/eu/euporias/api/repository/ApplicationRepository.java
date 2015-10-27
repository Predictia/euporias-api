package eu.euporias.api.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import eu.euporias.api.model.Application;

public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {

	@RestResource(path = "name", rel = "name")
	public Application findByName(String name);
	
}
