package eu.euporias.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Outcome;
import eu.euporias.api.model.Product;

@RepositoryRestResource(excerptProjection = SimpleOutcomeProjection.class)
public interface OutcomeRepository extends PagingAndSortingRepository<Outcome, Long> {

	@RestResource(path = "applicationProduct", rel = "applicationProduct")
	public Page<Outcome> findByApplicationAndProductOrderByLastModifiedDateDesc(@Param("application") Application application, @Param("product") Product product, Pageable page);
	
}
