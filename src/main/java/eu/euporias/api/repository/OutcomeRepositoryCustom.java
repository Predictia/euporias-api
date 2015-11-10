package eu.euporias.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Outcome;
import eu.euporias.api.model.ParameterValue;
import eu.euporias.api.model.Product;

public interface OutcomeRepositoryCustom {

	@PreAuthorize("#oauth2.clientHasRole(#application.authority) or #oauth2.clientHasRole('ROLE_ADMIN')")
	public Page<Outcome> findOutcomes(@P("application") Application application, Product product, List<ParameterValue> parameters, Pageable page);
	
}
