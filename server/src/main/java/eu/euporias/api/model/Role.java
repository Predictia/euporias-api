package eu.euporias.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;

@Entity
public class Role implements GrantedAuthority {

	private static final long serialVersionUID = 7088181304355358058L;

	/**
	 * Prefix for composing get {@link #getAuthority()} name, concatenated to {@link #getName()}
	 */
	public static final String AUTHORITY_PREFIX = "ROLE_";
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + "]";
	}
	
}
