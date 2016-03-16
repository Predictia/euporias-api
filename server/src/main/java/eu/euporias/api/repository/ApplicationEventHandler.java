package eu.euporias.api.repository;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import eu.euporias.api.model.Application;

@RepositoryEventHandler(Application.class)
@Transactional
@Component
public class ApplicationEventHandler {

	@HandleBeforeCreate
	public void handleApplicationCreate(Application application){
		application.setSecret(UUID.randomUUID().toString());
		application.setReadOnlySecret(UUID.randomUUID().toString());
	}
	
	@HandleBeforeDelete
	public void handleApplicationDelete(Application app){
		if(app == null) return;
		if(app.getId() == null) return;
		feedbackRepository.deleteByOutcomeApplication(app);
		outcomeRepository.deleteByApplication(app);
	}
	
	@Autowired private OutcomeRepository outcomeRepository;
	@Autowired private FeedbackRepository feedbackRepository;

}
