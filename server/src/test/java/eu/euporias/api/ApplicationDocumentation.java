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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.ParameterType;
import eu.euporias.api.model.Product;
import eu.euporias.api.repository.ApplicationRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebAppConfiguration
@ActiveProfiles(value = "test")
public class ApplicationDocumentation {
	
	@Test
	public void applicationsListExample() throws Exception {
		this.mockMvc
			.perform(get("/applications"))
			.andExpect(status().isOk())
			.andDo(document("applications-list-example",
				responseFields(
					Field.descriptors(Field._links, Field._embeddedApplications, Field.page)
				)
			));
	}

	@Test
	public void applicationsDeleteExample() throws Exception {
		Application testApp = applicationRepository.save(testApplication());
		try{
			this.mockMvc
				.perform(delete("/applications/" + testApp.getId()))
				.andExpect(status().isNoContent())
				.andDo(document("applications-delete-example"));
		}finally{
			applicationRepository.delete(testApp);
		}
	}
	
	@Test
	public void applicationsUpdateExample() throws Exception {
		Application testApp = applicationRepository.save(testApplication());
		Product product = testApp.getProducts().iterator().next();
		product.setId(null);
		product.setParameters(Stream.of(
			parameter("reportName", "new name of the report", ParameterType.TEXT, null),
			parameter("stationId", "code of the station", ParameterType.NUMBER, "0"),
			parameter("stationLon", "longitude the station", ParameterType.NUMBER, "#0.##"),
			parameter("stationLat", "latitude of the station", ParameterType.NUMBER, "#0.##")
		).collect(Collectors.toSet()));
		try{
			this.mockMvc
				.perform(put("/applications/" + testApp.getId())
					.contentType(MediaTypes.HAL_JSON)
					.content(this.objectMapper.writeValueAsString(testApp))
				)
				.andExpect(status().isNoContent())
				.andDo(document("applications-update-example", requestFields(
					Field.descriptors(Field.id, Field.name, Field.secret, Field.readOnlySecret, Field.products)
				)));
		}finally{
			applicationRepository.delete(applicationRepository.findByName(testApp.getName()));
		}
	}
	
	@Test
	public void applicationsGetExample() throws Exception {
		Application testApp = applicationRepository.save(testApplication());
		try{
			this.mockMvc
				.perform(get("/applications/" + testApp.getId()))
				.andExpect(status().isOk())
				.andDo(document("applications-get-example", responseFields(
					Field.descriptors(Field.name, Field.secret, Field.readOnlySecret, Field.products, Field._links)
				)));
		}finally{
			applicationRepository.delete(testApp);
		}
	}
	
	
	@Test
	public void applicationsCreateExample() throws Exception {
		Application testApp = testApplication();
		try{
			this.mockMvc
				.perform(post("/applications")
					.contentType(MediaTypes.HAL_JSON)
					.content(this.objectMapper.writeValueAsString(testApp))
				)
				.andExpect(status().isCreated())
				.andDo(document("applications-create-example", requestFields(
					Field.descriptors(Field.id, Field.name, Field.secret, Field.readOnlySecret, Field.products)
				)));
		}finally{
			applicationRepository.delete(applicationRepository.findByName(testApp.getName()));
		}
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
	
	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();			
	}
	
	@Rule
	public final RestDocumentation restDocumentation = new RestDocumentation("target/generated-snippets");	
	
	@Autowired private ObjectMapper objectMapper;
	@Autowired private WebApplicationContext context;
	@Autowired private ApplicationRepository applicationRepository;
	
}
