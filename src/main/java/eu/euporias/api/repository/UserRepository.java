package eu.euporias.api.repository;

import org.springframework.data.orient.commons.repository.annotation.Query;
import org.springframework.data.orient.object.repository.OrientObjectRepository;

import eu.euporias.api.model.User;

public interface UserRepository extends OrientObjectRepository<User> {
	
    @Query("select from user where email = ?")
	public User findByEmail(String email);
	
}
