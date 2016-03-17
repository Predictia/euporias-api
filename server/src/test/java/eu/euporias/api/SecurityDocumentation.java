package eu.euporias.api;

import static eu.euporias.api.ApiDocumentation.INDEX_LINKS_SNIPPET;
import static eu.euporias.api.ApiDocumentation.INDEX_RESPONSE_SNIPPET;
import static eu.euporias.api.MockObjects.testApplication;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.euporias.api.model.Application;
import eu.euporias.api.repository.ApplicationRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebAppConfiguration
public class SecurityDocumentation implements ResultHandler {
	
	@Test
	public void indexSecurityExample() throws Exception {
		this.mockMvc.perform(post("/oauth/token")
				.param("grant_type", "client_credentials")
				.param("scope", "read write")
				.header("Authorization", basicDigestHeaderValue(application.getName(), application.getSecret()))
			)
			.andExpect(status().isOk())
			.andDo(this)
			.andDo(document("token-get-example"));
		this.mockMvc.perform(get("/").header("Authorization", tokenHeaderValue()))
			.andExpect(status().isOk())
			.andDo(document("token-use-example",
				INDEX_LINKS_SNIPPET,
				INDEX_RESPONSE_SNIPPET
			));	
	}
	
	private String token;
	
	@Override
	public void handle(MvcResult result) throws Exception {
		JsonNode response = objectMapper.reader().readTree(result.getResponse().getContentAsString());
		token = response.findPath("access_token").asText();
		LOGGER.debug("Retrieved auth token: {}", token);
	}
	
	private MockMvc mockMvc;
	
	private Application application;
	
	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.addFilters(filterChainProxy)
			.apply(springSecurity())
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		loginAsAdmin();
		createTestApp();
	}
	
	private void loginAsAdmin() throws Exception{
		this.mockMvc.perform(post("/oauth/token")
				.param("grant_type", "client_credentials")
				.param("scope", "read write")
				.header("Authorization", basicDigestHeaderValue(ADMIN_USERNAME, defaultAdminPassword))
			)
			.andExpect(status().isOk())
			.andDo(this);
	}
	
	private void createTestApp() throws Exception{
		Application testApp = testApplication();
		this.mockMvc.perform(
			post("/applications")
				.header("Authorization", tokenHeaderValue())
				.contentType(MediaTypes.HAL_JSON)
				.content(this.objectMapper.writeValueAsString(testApp))
			)
			.andExpect(status().isCreated());
		this.application = applicationRepository.findByName(testApp.getName());
	}
	
	@After
	public void cleanUp() throws Exception {
		loginAsAdmin();
		this.mockMvc
			.perform(delete("/applications/" + application.getId())
				.header("Authorization", tokenHeaderValue())
			)
			.andExpect(status().isNoContent());
	}
	
	private String tokenHeaderValue(){
		return "Bearer " + token;
	}
	
	private static String basicDigestHeaderValue(String username, String password){
		return "Basic " + new String(Base64.encodeBase64((username + ":" + password).getBytes()));
	}
	
	private static final String ADMIN_USERNAME = "admin@api.euporias.eu";
	
	private @Value("${default.admin.password}") String defaultAdminPassword;

	@Rule
	public final RestDocumentation restDocumentation = new RestDocumentation("target/generated-snippets");	
	
	@Autowired private ObjectMapper objectMapper;
	@Autowired private WebApplicationContext context;
	@Autowired private ApplicationRepository applicationRepository;
	@Autowired private FilterChainProxy filterChainProxy;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityDocumentation.class);

}
