package eu.euporias.api.model;

import java.util.HashMap;

import org.hibernate.validator.constraints.NotBlank;

import com.orientechnologies.orient.core.annotation.OId;
import com.orientechnologies.orient.core.annotation.OVersion;

public class Product {
	
	@OId
	private String id;

	@OVersion
	private Long version;
	
	@NotBlank
	private String prototype;
	
	@NotBlank
	private String name;
	
	private HashMap<String, Object> parameters;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

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
