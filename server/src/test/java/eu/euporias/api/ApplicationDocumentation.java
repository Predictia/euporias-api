package eu.euporias.api;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Base64;
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
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Parameter;
import eu.euporias.api.model.ParameterType;
import eu.euporias.api.model.Product;
import eu.euporias.api.repository.ApplicationRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebAppConfiguration
@TestExecutionListeners(listeners={ServletTestExecutionListener.class,
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class,
	WithSecurityContextTestExecutionListener.class}
)
public class ApplicationDocumentation {
	
	@Test
	public void applicationsListExample() throws Exception {
		this.mockMvc.perform(
				get("/applications")
				.header("Authorization", tokenHeaderValue())
			)
			.andExpect(status().isOk())
			.andDo(document("applications-list-example",
				responseFields(
					fieldWithPath("_links").description("<<resources-index-links,Links>> to other resources"),
					fieldWithPath("_embedded.applications").description("An array of <<resources-application, Application resources>>"),
					fieldWithPath("page").description("Results pagination details")
				)
			));
	}
	
	@Test
	public void applicationsCreateExample() throws Exception {
		Application testApp = applicationRepository.save(testApplication());
		try{
			this.mockMvc.perform(
				post("/applications")
					.header("Authorization", tokenHeaderValue())
					
					.contentType(MediaTypes.HAL_JSON)
					.content(
						this.objectMapper.writeValueAsString(testApp))
					)
				.andExpect(status().isCreated())
				.andDo(document("applications-create-example",
					requestFields(
						fieldWithPath("name").description("The name of the application"),
						fieldWithPath("products").description("Array of products, each with name and parameters")
					)
				));
		}finally{
			applicationRepository.delete(testApp);
		}
	}

	private static Application testApplication(){
		Application testApp = new Application();
		testApp.setName("TEST");
		Product product = new Product();
		product.setName("test reports");
		product.setParameters(Stream.of(
			parameter("reportName", "name of the report", ParameterType.TEXT, null)
		).collect(Collectors.toSet()));
		testApp.setProducts(Stream.of(product).collect(Collectors.toSet()));
		return testApp;
	}
	
	private static Parameter parameter(String name, String description, ParameterType type, String format){
		Parameter parameter = new Parameter();
		parameter.setName(name);
		parameter.setDescription(description);
		parameter.setParameterType(type);
		parameter.setFormat(format);
		return parameter;
	}
	
	private MockMvc mockMvc;
	
	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.addFilters(filterChainProxy)
			.apply(springSecurity())
			.apply(documentationConfiguration(this.restDocumentation))
			.build();			
		getToken();
	}

	private void getToken() throws Exception{
		mockMvc.perform(post("/oauth/token")
				.param("grant_type", "client_credentials")
				.param("scope", "read write")
				.header("Authorization", basicDigestHeaderValue())
			)
			.andExpect(status().isOk())
			.andDo(new ResultHandler() {
				@Override
				public void handle(MvcResult result) throws Exception {
					JsonNode response = objectMapper.reader().readTree(result.getResponse().getContentAsString());
					token = response.findPath("access_token").asText();
					LOGGER.debug("Retrieved auth token: {}", token);
				}
			});
	}
	
	private String token;
	
	private String tokenHeaderValue(){
		return "Bearer " + token;
	}
	
	private String basicDigestHeaderValue(){
		return "Basic " + new String(Base64.encodeBase64((ADMIN_USERNAME + ":" + defaultAdminPassword).getBytes()));
	}
	
	private static final String ADMIN_USERNAME = "admin@api.euporias.eu";
	
	private @Value("${default.admin.password}") String defaultAdminPassword;

	@Rule
	public final RestDocumentation restDocumentation = new RestDocumentation("target/generated-snippets");	
	
	@Autowired private ObjectMapper objectMapper;
	@Autowired private WebApplicationContext context;
	@Autowired private ApplicationRepository applicationRepository;
	@Autowired private FilterChainProxy filterChainProxy;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDocumentation.class);
	
}
