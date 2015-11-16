package eu.euporias.api.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RequestDetails implements Serializable {
	
	private static final long serialVersionUID = 4969226120127689041L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String remoteAddress, userAgent, authName;
	
	private String oAuth2RequestParameters;
	
	private String oAuth2Scope;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getAuthName() {
		return authName;
	}

	public void setAuthName(String authName) {
		this.authName = authName;
	}

	public String getoAuth2RequestParameters() {
		return oAuth2RequestParameters;
	}

	public void setoAuth2RequestParameters(String oAuth2RequestParameters) {
		this.oAuth2RequestParameters = oAuth2RequestParameters;
	}

	public String getoAuth2Scope() {
		return oAuth2Scope;
	}

	public void setoAuth2Scope(String oAuth2Scope) {
		this.oAuth2Scope = oAuth2Scope;
	}

	@Override
	public String toString() {
		return "RequestDetails [id=" + id + "]";
	}
	
}