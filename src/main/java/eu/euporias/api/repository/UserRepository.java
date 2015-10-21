package eu.euporias.api.repository;

import org.springframework.data.orient.object.repository.OrientObjectRepository;

import eu.euporias.api.model.User;

public interface UserRepository extends OrientObjectRepository<User> {
	
}
