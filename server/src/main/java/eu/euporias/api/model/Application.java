package eu.euporias.api.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;

@Entity
public class Application implements GrantedAuthority {

	private static final long serialVersionUID = -7769760803284902278L;

	/**
	 * Prefix for composing get {@link #getAuthority()} name, concatenated to {@link #getName()}
	 */
	public static final String AUTHORITY_PREFIX = "APP_";
	
	@Override
	public String getAuthority() {
		return AUTHORITY_PREFIX + getName();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	@Column(unique = true) 
	private String name;
	
	private String secret, readOnlySecret;
	
	@OneToMany(cascade={CascadeType.ALL}, orphanRemoval = true)
	private Set<Product> products;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecret() {
		return secret;
	}

	public String getReadOnlySecret() {
		return readOnlySecret;
	}

	public void setReadOnlySecret(String readOnlySecret) {
		this.readOnlySecret = readOnlySecret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	@Override
	public String toString() {
		return "Application [id=" + id + "]";
	}
	
}
