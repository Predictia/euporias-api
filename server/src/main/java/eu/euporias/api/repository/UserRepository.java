package eu.euporias.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import eu.euporias.api.model.User;

@RepositoryRestResource(exported=false)
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	
	@RestResource(exported=false)
	public User findByEmail(@Param("email") String email);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public Page<User> findAll(Pageable pageable);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public User findOne(Long aLong);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public void deleteAll();

	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public void delete(Iterable<? extends User> entities);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public void delete(User id);

	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public <T extends User> T save(T entity);

	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public <T extends User> Iterable<T> save(Iterable<T> entities);
	
}
