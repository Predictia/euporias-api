package eu.euporias.api.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import eu.euporias.api.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	
	public User findByEmail(String email);
	
}
