package eu.euporias.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Outcome;
import eu.euporias.api.model.ParameterValue;
import eu.euporias.api.model.Product;

public interface OutcomeRepositoryCustom {

	public Page<Outcome> findOutcomesByParameters(Application application, Product product, List<ParameterValue> parameters, Pageable page);
	
}
