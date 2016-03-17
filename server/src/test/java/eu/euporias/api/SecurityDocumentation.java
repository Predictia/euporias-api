package eu.euporias.api;

import static eu.euporias.api.MockObjects.testApplication;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.euporias.api.model.Application;
import eu.euporias.api.repository.ApplicationRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebAppConfiguration
public class SecurityDocumentation {
	
	@Test
	public void indexSecurityExample() throws Exception {
		tokenHolder.login(mockMvc, application.getName(), application.getSecret(), document("token-get-example"));
		this.mockMvc.perform(get("/").header("Authorization", tokenHolder.tokenHeaderValue()))
			.andExpect(status().isOk())
			.andDo(document("index-example",
				INDEX_LINKS_SNIPPET,
				INDEX_RESPONSE_SNIPPET
			));	
	}
	
	static final Snippet INDEX_LINKS_SNIPPET = links(
		linkWithRel("applications").description("The <<resources-applications,Applications resource>>"),
		linkWithRel("outcomes").description("The <<resources-outcomes,Outcomes resource>>"),
		linkWithRel("profile").description("The ALPS profile for the service")
	);
	
	static final Snippet INDEX_RESPONSE_SNIPPET = responseFields(
		fieldWithPath("_links").description("<<resources-index-links,Links>> to other resources")
	);
	
	private MockMvc mockMvc;
	
	private Application application;
	
	private TokenHolder tokenHolder;
	
	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(springSecurity())
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		this.tokenHolder = new TokenHolder(objectMapper);
		loginAsAdmin();
		createTestApp();
	}
	
	private void loginAsAdmin() throws Exception{
		tokenHolder.login(mockMvc, ADMIN_USERNAME, defaultAdminPassword);
	}
	
	private void createTestApp() throws Exception{
		Application testApp = testApplication();
		this.mockMvc.perform(
			post("/applications")
				.header("Authorization", tokenHolder.tokenHeaderValue())
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
				.header("Authorization", tokenHolder.tokenHeaderValue())
			)
			.andExpect(status().isNoContent());
	}
	
	private static final String ADMIN_USERNAME = "admin@api.euporias.eu";
	
	private @Value("${default.admin.password}") String defaultAdminPassword;

	@Rule
	public final RestDocumentation restDocumentation = new RestDocumentation("target/generated-snippets");	
	
	@Autowired private ObjectMapper objectMapper;
	@Autowired private WebApplicationContext context;
	@Autowired private ApplicationRepository applicationRepository;

}
