package eu.euporias.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.euporias.api.model.User;
import eu.euporias.api.repository.UserRepository;

@Service("userService")
public class UserServiceImpl extends AbstractCrateService<User, UserRepository, String> implements UserService {

	@Autowired
    public UserServiceImpl(UserRepository userRepository) {
		super(User.class, userRepository);
	}

}

