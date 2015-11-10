package eu.euporias.api.repository;

import javax.transaction.Transactional;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.access.prepost.PreAuthorize;

import eu.euporias.api.model.User;

@RepositoryEventHandler(User.class)
@Transactional
public class UserEventHandler {

	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	@HandleBeforeCreate
	public void handleUserCreate(User user){
		// security check	
	}
	
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	@HandleBeforeSave
	public void handleUserSave(User user){
		// security check
	}
	
}
