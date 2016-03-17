package eu.euporias.api;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class TokenHolder implements ResultHandler {
	
	private final ObjectMapper objectMapper;
	
	public TokenHolder(ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
	}

	public void login(MockMvc mockMvc, String username, String password) throws Exception {
		login(mockMvc, username, password, null);
	}
	
	public void login(MockMvc mockMvc, String username, String password, ResultHandler resultHandler) throws Exception {
		ResultActions actions = mockMvc.perform(post("/oauth/token")
				.param("grant_type", "client_credentials")
				.param("scope", "read write")
				.header("Authorization", basicDigestHeaderValue(username, password))
			)
			.andExpect(status().isOk())
			.andDo(this);
		if(resultHandler != null){
			actions.andDo(resultHandler);
		}
	}

	private static String basicDigestHeaderValue(String username, String password){
		return "Basic " + new String(Base64.encodeBase64((username + ":" + password).getBytes()));
	}
	
	private String token;
	
	@Override
	public void handle(MvcResult result) throws Exception {
		JsonNode response = objectMapper.reader().readTree(result.getResponse().getContentAsString());
		token = response.findPath("access_token").asText();
		LOGGER.debug("Retrieved auth token: {}", token);
	}
	
	public String tokenHeaderValue(){
		return "Bearer " + token;
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenHolder.class);
	
}