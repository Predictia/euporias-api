package eu.euporias.api.model;

import java.util.HashMap;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.crate.core.mapping.annotations.Table;

@Table(name="products", refreshInterval=500, numberOfReplicas="0-all")
public class Product {

	@Id
	@NotBlank
	private String productId;
	
	@NotBlank
	private String prototype;
	
	@NotBlank
	private String name;
	
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	private HashMap<String, Object> parameters;

	public String getPrototype() {
		return prototype;
	}

	public void setPrototype(String prototype) {
		this.prototype = prototype;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}
	
}
