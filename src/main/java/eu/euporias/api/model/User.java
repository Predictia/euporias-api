package eu.euporias.api.model;

import java.util.Date;
import java.util.HashMap;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import com.orientechnologies.orient.core.annotation.OId;
import com.orientechnologies.orient.core.annotation.OVersion;

public class User {

	@OId
	private String id;

	@OVersion
	private Long version;
	
    @Email
    @NotBlank
    private String email;
    private String firstName;
    private String lastName;
    private Date dateJoined;
    private String[] prototypes;
    private HashMap<String, Object> attributes;
    
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

	public String getFirstName() {
        return firstName;
    }
	
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return String.format("%s %s", this.firstName, this.lastName);
    }
    
    public void setFullName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateJoined() {
		return dateJoined;
	}

	public void setDateJoined(Date dateJoined) {
		this.dateJoined = dateJoined;
	}

	public String[] getPrototypes() {
		return prototypes;
	}

	public void setPrototypes(String[] prototypes) {
		this.prototypes = prototypes;
	}

	public HashMap<String, Object> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

}