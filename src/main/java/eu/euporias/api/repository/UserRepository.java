package eu.euporias.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import eu.euporias.api.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	
	@RestResource(exported=false)
	public User findByEmail(@Param("email") String email);
	
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Page<User> findAll(Pageable pageable);
	
	@Override
	@PostAuthorize("returnObject.email == principal.username or hasRole('ROLE_ADMIN')")
	public User findOne(Long aLong);
	
}
