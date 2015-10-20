package eu.euporias.api.service;

import eu.euporias.api.model.User;
import eu.euporias.api.repository.UserRepository;

public interface UserService extends CrateService<User, UserRepository, String> {

	public UserRepository getRepo();
	
}
