package eu.euporias.api;



import static eu.euporias.api.MockObjects.testApplication;
import static eu.euporias.api.MockObjects.testOutcome;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import eu.euporias.api.repository.ApplicationRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebAppConfiguration
@ActiveProfiles(value = "test")
public class OutcomeDocumentation {
	
	@Test
	public void outcomesCreateExample() throws Exception {
		Application testApp = applicationRepository.save(testApplication());
		try{
			this.mockMvc
				.perform(post("/outcomes")
					.contentType(MediaTypes.HAL_JSON)
					.content(this.objectMapper.writeValueAsString(testOutcome(testApp)))
				)
				.andExpect(status().isCreated())
				.andDo(document("outcomes-create-example", requestFields(
					Field.descriptors(
						Field.application, Field.product,
						Field.parameters, Field.outcomeType, Field.mimeType
					)
				)));
		}finally{
			this.mockMvc.perform(delete("/applications/" + testApp.getId()));
		}
	}
	
	private enum Field{
		
		id("The id code of the outcome"),
		application("The application that generated the outcome"),
		product("The product that generated the outcome"),
		parameters("The set of parameters of the outcome"),
		outcomeType("The type of the outcome. Supported types are: NUMBER, TEXT, EMBEDDED_FILE, FILE"),
		format("The format of the outcome result string"),
		mimeType("The mime type the outcome (makes sense with the EMBEDDED_FILE and FILE outcome types)"),
		results("Results of the outcome"),
		lastModifiedDate("Last modified date"),
		_links("<<resources-index-links,Links>> to other resources"),
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
