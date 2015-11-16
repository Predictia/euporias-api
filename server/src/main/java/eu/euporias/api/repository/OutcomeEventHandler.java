package eu.euporias.api.repository;

import static eu.euporias.api.util.Encoder.decodeBase64ToFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import eu.euporias.api.model.Outcome;
import eu.euporias.api.model.OutcomeType;
import eu.euporias.api.model.Product;
import eu.euporias.api.service.StorageService;

@RepositoryEventHandler(Outcome.class)
@Transactional
public class OutcomeEventHandler {

	@HandleBeforeCreate
	public void handleOutcomeCreate(Outcome outcome) throws IOException {
		assignExistingProduct(outcome);
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
		Optional<Product> eProduct = outcome.getApplication().getProducts().stream()
			.filter(p -> p.getName().equals(productName))
			.findFirst();
		if(!eProduct.isPresent()){
			throw new IllegalArgumentException("Product with name " + productName + " not found");
		}
		outcome.setProduct(eProduct.get());
	}
	
	@HandleBeforeSave
	public void handleOutcomeSave(Outcome outcome) throws IOException {
		outcome.setLastModifiedDate(new Date());
	}
	
	@HandleBeforeDelete
	public void handleOutcomeDelete(Outcome outcome) {
		if(FILE_TYPES.contains(outcome.getOutcomeType())){
			outcome.getResults()
				.forEach((relativePath) -> {
					File f = storageService.expandFilePath(relativePath);
					LOGGER.info("Deleting file {} related to outcome #{}", f, outcome.getId());
					f.delete();
				});
		}
	}
	
	@Autowired private StorageService storageService;
	
	private static final Set<OutcomeType> FILE_TYPES = EnumSet.of(OutcomeType.EMBEDDED_FILE, OutcomeType.FILE);	
	private static final Logger LOGGER = LoggerFactory.getLogger(OutcomeEventHandler.class);

}