package eu.euporias.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.euporias.api.model.Outcome;
import eu.euporias.api.repository.OutcomeRepository;

@Service("outcomeService")
public class OutcomeServiceImpl extends AbstractService<Outcome, OutcomeRepository> implements OutcomeService {

	@Autowired
    public OutcomeServiceImpl(OutcomeRepository outcomeRepository) {
		super(Outcome.class, outcomeRepository);
	}

}

