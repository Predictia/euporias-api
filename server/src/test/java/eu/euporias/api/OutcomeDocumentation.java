package eu.euporias.api;

import static eu.euporias.api.MockObjects.TEST_PRODUCT_NAME;
import static eu.euporias.api.MockObjects.embeddedOutcome;
import static eu.euporias.api.MockObjects.fileWithRandomContent;
import static eu.euporias.api.MockObjects.testApplication;
import static eu.euporias.api.MockObjects.testOutcome;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.fileUpload;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.euporias.api.model.Application;
import eu.euporias.api.repository.ApplicationRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebAppConfiguration
public class OutcomeDocumentation {
	
	@Test
	public void outcomesCreateEmbeddedExample() throws Exception {
		this.mockMvc
			.perform(post("/outcomes")
				.header("Authorization", tokenHolder.tokenHeaderValue())
				.contentType(MediaTypes.HAL_JSON)
				.content(this.objectMapper.writeValueAsString(embeddedOutcome(application.getId())))
			)
			.andExpect(status().isCreated())
			.andDo(document("outcomes-create-embedded-example", requestFields(
				Field.descriptors(
					Field.application, Field.product, Field.results,
					Field.parameters, Field.outcomeType, Field.mimeType
				)
			)));
	}
	
	@Test
	public void outcomesCreateExample() throws Exception {
		createTestOutcome()
			.andDo(document("outcomes-create-example", requestFields(
				Field.descriptors(
					Field.application, Field.product,
					Field.parameters, Field.outcomeType, Field.mimeType
				)
			)));
	}

	private ResultActions createTestOutcome() throws Exception{
		return this.mockMvc
			.perform(post("/outcomes")
				.header("Authorization", tokenHolder.tokenHeaderValue())
				.contentType(MediaTypes.HAL_JSON)
				.content(this.objectMapper.writeValueAsString(testOutcome(application.getId())))
			)
			.andExpect(status().isCreated());
	}
	
	@Test
	public void outcomesAttachExample() throws Exception {
		String location = createTestOutcome().andReturn()
			.getResponse().getHeader("Location");
		uploadFile(location)
			.andDo(document("outcomes-attach-example"));
		
	}
	
	private ResultActions uploadFile(String location) throws Exception{
		File file = fileWithRandomContent();
		MockMultipartFile upload = new MockMultipartFile("file", new FileInputStream(file));
		return this.mockMvc
			.perform(fileUpload("/attachments/outcomes/" + idFromPath(location))
				.file(upload)
				.header("Authorization", tokenHolder.tokenHeaderValue())
			)
			.andExpect(status().isOk());
	}
	
	private static String idFromPath(String location){
		String[] els = location.split("/");
		return els[els.length - 1];
	}
	
	@Test
	public void outcomesAttachRetrieveExample() throws Exception {
		String location = createTestOutcome().andReturn()
			.getResponse().getHeader("Location");
		uploadFile(location);
		this.mockMvc
			.perform(get("/attachments/outcomes/" + idFromPath(location) + "/0").header("Authorization", tokenHolder.tokenHeaderValue()))
			.andExpect(status().isOk())
			.andDo(document("outcomes-attach-retrieve-example"));
	}
	
	@Test
	public void outcomesAttachDeleteExample() throws Exception {
		String location = createTestOutcome().andReturn()
			.getResponse().getHeader("Location");
		uploadFile(location);
		this.mockMvc
			.perform(delete("/attachments/outcomes/" + idFromPath(location) + "/0").header("Authorization", tokenHolder.tokenHeaderValue()))
			.andExpect(status().isOk())
			.andDo(document("outcomes-attach-delete-example"));
		
	}
	
	@Test
	public void outcomesUpdateExample() throws Exception {
		String location = createTestOutcome().andReturn()
			.getResponse().getHeader("Location");
		ObjectNode updatedOutcome = testOutcome(application.getId());
		updatedOutcome.set("mimeType", JsonNodeFactory.instance.textNode("other mime type"));
		this.mockMvc
			.perform(put("/outcomes/" + idFromPath(location))
				.header("Authorization", tokenHolder.tokenHeaderValue())
				.contentType(MediaTypes.HAL_JSON)
				.content(this.objectMapper.writeValueAsString(updatedOutcome))
			)
			.andExpect(status().isNoContent())
			.andDo(document("outcomes-update-example", requestFields(
				Field.descriptors(
					Field.application, Field.product,
					Field.parameters, Field.outcomeType, Field.mimeType
				)
			)));
	}
	
	@Test
	public void outcomesDeleteExample() throws Exception {
		String location = createTestOutcome().andReturn()
			.getResponse().getHeader("Location");
		ObjectNode updatedOutcome = testOutcome(application.getId());
		updatedOutcome.set("mimeType", JsonNodeFactory.instance.textNode("other mime type"));
		this.mockMvc
			.perform(delete("/outcomes/" + idFromPath(location)).header("Authorization", tokenHolder.tokenHeaderValue()))
			.andExpect(status().isNoContent())
			.andDo(document("outcomes-delete-example"));
	}
	
	@Test
	public void outcomesSearchMetaExample() throws Exception {
		createTestOutcome();
		this.mockMvc
			.perform(get("/outcomes/search/metadata")
				.header("Authorization", tokenHolder.tokenHeaderValue())
				.param("application", application.getId().toString())
				.param("product", TEST_PRODUCT_NAME)
				.param("parameter", "reportName")
			)
			.andExpect(status().isOk())
			.andDo(document("outcomes-search-meta-example", responseFields(
				Field.descriptors(Field._links, Field._embedded, Field.page)
			)));
	}
	
	@Test
	public void outcomesSearchExample() throws Exception {
		createTestOutcome();
		this.mockMvc
			.perform(get("/outcomes/search/parameters")
				.header("Authorization", tokenHolder.tokenHeaderValue())
				.param("application", application.getId().toString())
				.param("product", TEST_PRODUCT_NAME)
				.param("reportName", "EUPORIAS PowerPoint Template")
			)
			.andExpect(status().isOk())
			.andDo(document("outcomes-search-example", responseFields(
				Field.descriptors(Field._links, Field._embeddedOutcomes, Field.page)
			)));
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
		_embeddedOutcomes("_embedded.outcomes", "An array of <<resources-outcomes, Outcome resources>>"),
		_embedded("An object with arrays of responses indexed by type"),
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
		createTestApp();
		tokenHolder.login(mockMvc, application.getName(), application.getSecret());
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
