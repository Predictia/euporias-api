package eu.euporias.api.service;

import eu.euporias.api.model.Outcome;
import eu.euporias.api.repository.OutcomeRepository;

public interface OutcomeService extends RepositoryService<Outcome, Long, OutcomeRepository> {

	public OutcomeRepository getRepo();
	
}