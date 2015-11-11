package eu.euporias.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import eu.euporias.api.model.Application;

public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {

	@RestResource(exported = false)
	public Application findByName(@Param("name") String name);
	
	@Override
	@PostAuthorize("#oauth2.clientHasRole(returnObject.authority) or #oauth2.clientHasRole('ROLE_ADMIN')")
	public Application findOne(Long id);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public Page<Application> findAll(Pageable pageable);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public Page<Application> findAll(Sort sort);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public void deleteAll();

	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public void delete(Iterable<? extends Application> entities);

	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public void delete(Application id);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole(#application.authority) or #oauth2.clientHasRole('ROLE_ADMIN')")
	public <T extends Application> T save(@P("application")  T entity);

	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public <T extends Application> Iterable<T> save(Iterable<T> entities);
	
}
