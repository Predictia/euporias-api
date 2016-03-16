package eu.euporias.api.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import eu.euporias.api.model.User;

@RepositoryRestResource(exported=false)
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	
	public User findByEmail(@Param("email") String email);
	
}
