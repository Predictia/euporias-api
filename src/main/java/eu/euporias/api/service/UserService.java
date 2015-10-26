package eu.euporias.api.service;

import eu.euporias.api.model.User;
import eu.euporias.api.repository.UserRepository;

public interface UserService extends RepositoryService<User, Long, UserRepository> {

	public UserRepository getRepo();
	
}
