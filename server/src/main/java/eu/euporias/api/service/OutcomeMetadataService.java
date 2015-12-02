package eu.euporias.api.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Product;

public interface OutcomeMetadataService {

	/** Find number of available parameter values by parameter name
	 * @param application
	 * @param product
	 * @param parameterName
	 * @return Total number of parameter values, must not be null
	 */
	public Integer countByParameterName(Application application, Product product, String parameterName);
	
	/**
	 * Find available parameter values by parameter name 
	 */
	public List<String> findByParameterName(Application application, Product product, String parameterName, Pageable pageable);
	
}
