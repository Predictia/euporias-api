package eu.euporias.api.model;

import java.util.HashMap;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.crate.core.mapping.annotations.Table;

@Table(name="outcomes", refreshInterval=500, numberOfReplicas="0-all")
public class Outcome {
	
	@Id
	@NotBlank
	private String outcomeId;
	
	@NotBlank
	private String productId;
	
	private HashMap<String, Object> parameters;
	
	private Object[] results;
	
	public String getOutcomeId() {
		return outcomeId;
	}

	public void setOutcomeId(String outcomeId) {
		this.outcomeId = outcomeId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public HashMap<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}

	public Object[] getResults() {
		return results;
	}

	public void setResults(Object[] results) {
		this.results = results;
	}
	
}
