package eu.euporias.api.repository;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import eu.euporias.api.model.Application;

@RepositoryEventHandler(Application.class)
@Transactional
public class ApplicationEventHandler {

	@HandleBeforeCreate
	public void handleApplicationCreate(Application application){
		application.setSecret(UUID.randomUUID().toString());	
	}

}
