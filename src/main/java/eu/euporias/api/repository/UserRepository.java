package eu.euporias.api.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import eu.euporias.api.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	
	@RestResource(path = "email", rel = "email")
	public User findByEmail(String email);
	
}
