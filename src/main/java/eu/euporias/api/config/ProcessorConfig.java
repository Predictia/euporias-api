package eu.euporias.api.config;

import static eu.euporias.api.util.Encoder.encodeFileToBase64;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

import eu.euporias.api.controller.AttachmentsController;
import eu.euporias.api.model.Outcome;
import eu.euporias.api.model.OutcomeType;
import eu.euporias.api.service.StorageService;

@Configuration
public class ProcessorConfig {

	@Bean
	public ResourceProcessor<Resource<Outcome>> outcomeProcessor(final StorageService storageService) {
		return new ResourceProcessor<Resource<Outcome>>() {
			@Override
			public Resource<Outcome> process(Resource<Outcome> resource) {
				Outcome outcome = resource.getContent();
				if(OutcomeType.EMBEDDED_FILE.equals(outcome.getOutcomeType())){
					outcome.setResults(
						outcome.getResults()
							.stream()
							.map((relativePath) -> encodeFileToBase64(storageService.expandFilePath(relativePath)))
							.collect(Collectors.toList())
					);
				}else if(OutcomeType.FILE.equals(outcome.getOutcomeType())){
					if(outcome.getResults() != null){
						for(int i = 0; i<outcome.getResults().size(); i++){
							resource.add(linkTo(methodOn(AttachmentsController.class).file(outcome.getId(), i, null)).withRel("attachment"));
						}
					}
					outcome.setResults(null);
				}
				return resource;
			}
		};
	}
	
}
