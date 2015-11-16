package eu.euporias.api.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class OutcomeAccess implements Serializable {

	private static final long serialVersionUID = 2015313999143591999L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@CreatedDate
	private Date created = new Date();
	
	@CreatedBy
	@OneToOne(cascade=CascadeType.ALL, orphanRemoval = true)
	private RequestDetails createdBy;
	
	private String methodName;
	
	@Column(length = 2048)
	private String arguments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public RequestDetails getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(RequestDetails createdBy) {
		this.createdBy = createdBy;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	@Override
	public String toString() {
		return "OutcomeAccess [id=" + id + "]";
	}
	
}
