package eu.euporias;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class JsonParserTests {

	@Test
	public void parseOutcomeId() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		String result = Resources.toString(getClass().getResource("/get-result.json"),Charsets.UTF_8);
		JsonNode node = mapper.readTree(result);
		String id = node.at("/_embedded/outcomes/0/_links/feedback/href").asText();
		Assert.assertTrue(!id.isEmpty());
	}
}
