package eu.euporias.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Outcome;
import eu.euporias.api.model.Product;

@RepositoryRestResource(excerptProjection = SimpleOutcomeProjection.class)
public interface OutcomeRepository extends PagingAndSortingRepository<Outcome, Long>, OutcomeRepositoryCustom {

	@RestResource(exported = false)
	@PreAuthorize("#oauth2.clientHasRole(#application.authority) or #oauth2.clientHasRole('ROLE_ADMIN')")
	public Page<Outcome> findByApplicationAndProductOrderByLastModifiedDateDesc(
		@Param("application") Application application, 
		@Param("product") Product product, 
		Pageable page
	);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public Page<Outcome> findAll(Sort pageable);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public Page<Outcome> findAll(Pageable pageable);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	void deleteAll();
	
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public void deleteByApplication(Application application);
	
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public void deleteByApplicationAndProduct(@Param("application") Application application, @Param("product") Product product);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	<S extends Outcome> Iterable<S> save(Iterable<S> entities);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	void delete(Iterable<? extends Outcome> entities);
	
	@Override
	@PostAuthorize("#oauth2.clientHasRole(returnObject.application.authority) or #oauth2.clientHasRole('ROLE_ADMIN')")
	public Outcome findOne(Long aLong);
	
	@Override
	@PreAuthorize("(#oauth2.clientHasRole(#outcome.application.authority) and #oauth2.hasScope('write')) or #oauth2.clientHasRole('ROLE_ADMIN')")
	public <S extends Outcome> S save(@P("outcome") S outcome);
	
	@Override
	@PreAuthorize("(#oauth2.clientHasRole(#outcome.application.authority) and #oauth2.hasScope('write')) or #oauth2.clientHasRole('ROLE_ADMIN')")
	public void delete(@P("outcome") Outcome outcome);
	
	@RestResource(exported=false)
	public Page<Outcome> findByIdInOrderByLastModifiedDateDesc(List<Long> id, Pageable page);
	
}
