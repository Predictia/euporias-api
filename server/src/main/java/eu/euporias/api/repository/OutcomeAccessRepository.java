package eu.euporias.api.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import eu.euporias.api.model.OutcomeAccess;

@RepositoryRestResource(exported  = false)
public interface OutcomeAccessRepository extends PagingAndSortingRepository<OutcomeAccess, Long>{
	
}
