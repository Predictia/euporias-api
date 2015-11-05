package eu.euporias.api.repository;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import eu.euporias.api.model.User;

@RepositoryEventHandler(User.class)
@Transactional
public class UserEventHandler {

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@HandleBeforeCreate
	public void handleUserCreate(User user){
		user.setPassword(md5PasswordEncoder.encodePassword(user.getPassword(), null));;	
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@HandleBeforeSave
	public void handleUserSave(User user){
		// security check
	}
	
	@Autowired private Md5PasswordEncoder md5PasswordEncoder;

}
