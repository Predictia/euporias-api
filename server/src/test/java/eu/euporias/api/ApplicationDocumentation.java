package eu.euporias.api;

import static eu.euporias.api.MockObjects.parameter;
import static eu.euporias.api.MockObjects.testApplication;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.ParameterType;
import eu.euporias.api.model.Product;
import eu.euporias.api.repository.ApplicationRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebAppConfiguration
@Transactional @Rollback(false)
public class ApplicationDocumentation {
	
	@Test
	public void applicationsOperationsExample() throws Exception {
		applicationsCreateExample();
		try{
			applicationsGetExample();
			applicationsListExample();
			applicationsUpdateExample();
		}finally{
			applicationsDeleteExample();
		}
	}
	
	private void applicationsListExample() throws Exception {
		this.mockMvc
			.perform(get("/applications").header("Authorization", tokenHolder.tokenHeaderValue()))
			.andExpect(status().isOk())
			.andDo(document("applications-list-example",
				responseFields(
					Field.descriptors(Field._links, Field._embeddedApplications, Field.page)
				)
			));
	}
	
	private void applicationsCreateExample() throws Exception {
		Application testApp = testApplication();
		this.mockMvc
			.perform(post("/applications")
				.header("Authorization", tokenHolder.tokenHeaderValue())
				.contentType(MediaTypes.HAL_JSON)
				.content(this.objectMapper.writeValueAsString(testApp))
			)
			.andExpect(status().isCreated())
			.andDo(document("applications-create-example", requestFields(
				Field.descriptors(Field.name, Field.products)
			)));
		this.application = applicationRepository.findByName(testApp.getName());
	}

	private void applicationsDeleteExample() throws Exception {
		this.mockMvc
			.perform(delete("/applications/" + application.getId()).header("Authorization", tokenHolder.tokenHeaderValue()))
			.andExpect(status().isNoContent())
			.andDo(document("applications-delete-example"));		
	}
	
	private void applicationsUpdateExample() throws Exception {
		Product product = application.getProducts().iterator().next();
		product.setId(null);
		product.setParameters(Stream.of(
			parameter("reportName", "new name of the report", ParameterType.TEXT, null),
			parameter("stationId", "code of the station", ParameterType.NUMBER, "0"),
			parameter("stationLon", "longitude the station", ParameterType.NUMBER, "#0.##"),
			parameter("stationLat", "latitude of the station", ParameterType.NUMBER, "#0.##")
		).collect(Collectors.toSet()));
		this.mockMvc
			.perform(put("/applications/" + application.getId())
				.header("Authorization", tokenHolder.tokenHeaderValue())
				.contentType(MediaTypes.HAL_JSON)
				.content(this.objectMapper.writeValueAsString(application))
			)
			.andExpect(status().isNoContent())
			.andDo(document("applications-update-example", requestFields(
				Field.descriptors(Field.id, Field.name, Field.secret, Field.readOnlySecret, Field.products)
			)));
	}
	
	private void applicationsGetExample() throws Exception {
		this.mockMvc
			.perform(get("/applications/" + application.getId()).header("Authorization", tokenHolder.tokenHeaderValue()))
			.andExpect(status().isOk())
			.andDo(document("applications-get-example", responseFields(
				Field.descriptors(Field.name, Field.products, Field.secret, Field.readOnlySecret, Field._links)
			)));
	}
	
	private enum Field{
		
		id("The id code of the application"), 
		name("The name of the application"), 
		secret("The read/write secret of the application. Use it in combination with the application name as username."), 
		readOnlySecret("The read only secret of the application. Use it in combination with the application name, with a suffix '.ro' as username."), 
		products("Array of products, each with name and parameters"),
		_links("<<resources-index-links,Links>> to other resources"),
		_embeddedApplications("_embedded.applications", "An array of <<resources-applications, Application resources>>"),
		page("Results pagination details")
		;
		
		private Field(String path, String description) {
			this.path = path;
			this.description = description;
		}
		
		private Field(String description) {
			this.description = description;
			this.path = name();
		}

		private final String path;
		
		private final String description;
		
		public static FieldDescriptor[] descriptors(Field... fields){
			return Stream.of(fields)
				.map(f -> fieldWithPath(f.path).description(f.description))
				.toArray(s -> new FieldDescriptor[s]);
		}
		
	}
	
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
		Application testApp = applicationRepository.findByName(testApplication().getName());
		if(testApp != null){
			this.mockMvc
				.perform(delete("/applications/" + testApp.getId()).header("Authorization", tokenHolder.tokenHeaderValue()))
				.andExpect(status().isNoContent());
		}
	}
	
	private void loginAsAdmin() throws Exception{
		tokenHolder.login(mockMvc, ADMIN_USERNAME, defaultAdminPassword);
	}

	private static final String ADMIN_USERNAME = "admin@api.euporias.eu";
	
	private @Value("${default.admin.password}") String defaultAdminPassword;
	
	@Rule
	public final RestDocumentation restDocumentation = new RestDocumentation("target/generated-snippets");	
	
	@Autowired private ObjectMapper objectMapper;
	@Autowired private WebApplicationContext context;
	@Autowired private ApplicationRepository applicationRepository;
	
}
