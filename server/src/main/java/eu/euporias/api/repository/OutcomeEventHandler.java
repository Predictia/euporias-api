package eu.euporias.api.repository;

import static eu.euporias.api.util.Encoder.decodeBase64ToFile;

import java.io.File;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Outcome;
import eu.euporias.api.model.OutcomeType;
import eu.euporias.api.model.ParameterValue;
import eu.euporias.api.model.Product;
import eu.euporias.api.service.StorageService;

@RepositoryEventHandler(Outcome.class)
@Transactional
@Component
public class OutcomeEventHandler {

	@HandleBeforeCreate
	public void handleOutcomeCreate(Outcome outcome) {
		assignExistingProduct(outcome);
		assertNewOutcome(outcome);
		outcome.setLastModifiedDate(new Date());
		if(OutcomeType.EMBEDDED_FILE.equals(outcome.getOutcomeType())){
			outcome.setResults(outcome.getResults().stream()
				.map((base64text) -> {
					try {
						File f = storageService.storeFile(decodeBase64ToFile(base64text));
						return storageService.relativeFilePath(f);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				})
				.collect(Collectors.toList())
			);
		}
	}
	
	private void assignExistingProduct(Outcome outcome){
		if(outcome.getProduct().getId() != null){
			return;
		}
		String productName = outcome.getProduct() != null ? 
			outcome.getProduct().getName() : 
			null;
		Application app = applicationRepository.findOne(outcome.getApplication().getId());
		Optional<Product> eProduct = app.getProducts().stream()
			.filter(p -> p.getName().equals(productName))
			.findFirst();
		if(!eProduct.isPresent()){
			throw new IllegalArgumentException("Product with name " + productName + " not found");
		}
		outcome.setProduct(eProduct.get());
	}
	
	private void assertNewOutcome(Outcome outcome) throws IllegalStateException {
		List<ParameterValue>parameters = outcome.getParameters().entrySet().stream()
			.map(e -> new ParameterValue(e.getKey(), e.getValue()))
			.collect(Collectors.toList())
			;
		Page<Outcome> page = outcomeRepository.findOutcomes(outcome.getApplication(), outcome.getProduct(), parameters, new PageRequest(0, 1));
		if(!page.getContent().isEmpty()){
			throw new IllegalStateException("Found another outcome (" + page.getContent().get(0).getId() + ") with the same parameter set, consider updating it");
		}
	}
	
	@HandleBeforeSave
	public void handleOutcomeSave(Outcome outcome) {
		assignExistingProduct(outcome);
		outcome.setLastModifiedDate(new Date());
	}
	
	@HandleBeforeDelete
	public void handleOutcomeDelete(Outcome outcome) {
		assignExistingProduct(outcome);
		if(FILE_TYPES.contains(outcome.getOutcomeType())){
			if(outcome.getResults() != null){
				outcome.getResults()
					.forEach((relativePath) -> {
						File f = storageService.expandFilePath(relativePath);
						LOGGER.info("Deleting file {} related to outcome #{}", f, outcome.getId());
						f.delete();
					});
			}
		}
	}
	
	@Autowired private OutcomeRepository outcomeRepository;
	@Autowired private ApplicationRepository applicationRepository;
	@Autowired private StorageService storageService;
	
	private static final Set<OutcomeType> FILE_TYPES = EnumSet.of(OutcomeType.EMBEDDED_FILE, OutcomeType.FILE);	
	private static final Logger LOGGER = LoggerFactory.getLogger(OutcomeEventHandler.class);

}
