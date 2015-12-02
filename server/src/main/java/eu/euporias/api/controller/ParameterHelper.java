package eu.euporias.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.util.MultiValueMap;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.ParameterValue;
import eu.euporias.api.model.Product;
import eu.euporias.api.repository.ApplicationRepository;
import eu.euporias.api.repository.ProductRepository;

/**
 * Support class for working with parameter maps. Supports {@link Application}
 * passed as id with {@value #APPLICATION_PARAMETER_KEY} key, and {@link Product}
 * passed either as id or as name with {@value #PRODUCT_PARAMETER_KEY} key
 * 
 * @author Max
 *
 */
class ParameterHelper {

	public static final String APPLICATION_PARAMETER_KEY = "application";
	public static final String PRODUCT_PARAMETER_KEY = "product";
	
	public ParameterHelper(
			ApplicationRepository applicationRepository,
			ProductRepository productRepository,
			MultiValueMap<String, String> parameters
		) {
		super();
		this.applicationRepository = applicationRepository;
		this.productRepository = productRepository;
		this.parameters = parameters;
	}

	private final ApplicationRepository applicationRepository;
	private final ProductRepository productRepository;
	private final MultiValueMap<String, String> parameters;
	
	public boolean hasApplicationAndProduct(){
		return ((parameters != null) && parameters.containsKey(APPLICATION_PARAMETER_KEY) && parameters.containsKey(PRODUCT_PARAMETER_KEY));
	}
	
	public Application application() {
		Long appId = Long.valueOf(parameters.getFirst(APPLICATION_PARAMETER_KEY));
		return applicationRepository.findOne(appId);
	}

	public Product product(Application application) {
		try {
			Long productId = Long.valueOf(parameters.getFirst(PRODUCT_PARAMETER_KEY));
			return productRepository.findOne(productId);
		} catch (NumberFormatException e) {
			String productName = parameters.getFirst(PRODUCT_PARAMETER_KEY);
			Optional<Product> eProduct = application.getProducts().stream()
				.filter(p -> p.getName().equals(productName)).findFirst();
			return eProduct.get();
		}
	}

	/**
	 * @param parameters
	 * @return Other parameters that are not application and product
	 */
	public List<ParameterValue> otherParameterValues() {
		List<ParameterValue> pvs = new ArrayList<>();
		for (String key : parameters.keySet()) {
			if (APPLICATION_PARAMETER_KEY.equalsIgnoreCase(key)) continue;
			if (PRODUCT_PARAMETER_KEY.equalsIgnoreCase(key)) continue;
			for (String value : parameters.get(key)) {
				pvs.add(new ParameterValue(key, value));
			}
		}
		return pvs;
	}

}
