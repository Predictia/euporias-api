package eu.euporias.api.repository;

import org.springframework.data.crate.repository.CrateRepository;

import eu.euporias.api.model.User;

public interface UserRepository extends CrateRepository<User, String> {
	
}
