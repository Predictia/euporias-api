package eu.euporias.api.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Product;

@RepositoryEventHandler(Application.class)
@Transactional
@Component
public class ApplicationEventHandler {

	@HandleBeforeCreate
	public void handleApplicationCreate(Application application){
		application.setSecret(UUID.randomUUID().toString());
		application.setReadOnlySecret(UUID.randomUUID().toString());
	}
	
	@HandleBeforeSave
	public void handleApplicationUpdate(Application updatedAppDetails){
		if(updatedAppDetails == null) return;
		if(updatedAppDetails.getId() == null) return;
		Application existingApp = applicationRepository.findOne(updatedAppDetails.getId());
		existingApp.setProducts(
			updatedAppDetails.getProducts().stream()
			.map(updatedProduct -> {
				if(updatedProduct.getId() == null){
					Optional<Product> eProduct = existingApp.getProducts().stream()
						.filter(p -> p.getName().equals(updatedProduct.getName())).findFirst();
					if(eProduct.isPresent()){
						updatedProduct.setId(eProduct.get().getId());
					}
				}
				return updatedProduct;
			})
			.collect(Collectors.toSet())
		);
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
	@Autowired private ApplicationRepository applicationRepository;

}
