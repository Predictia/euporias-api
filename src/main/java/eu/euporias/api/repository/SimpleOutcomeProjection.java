package eu.euporias.api.repository;

import java.util.Date;
import java.util.Map;

import org.springframework.data.rest.core.config.Projection;

import eu.euporias.api.model.Outcome;
import eu.euporias.api.model.OutcomeType;

@Projection(name = "simpleOutcome", types = { Outcome.class })
public interface SimpleOutcomeProjection {

	public OutcomeType getOutcomeType();
	public Map<String, String> getParameters();
	public String getMimeType();
	public String getFormat();
	public Date getLastModifiedDate();
	
}
