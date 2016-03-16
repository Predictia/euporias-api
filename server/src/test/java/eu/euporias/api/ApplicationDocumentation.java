package eu.euporias.api;

import static eu.euporias.api.MockObjects.testApplication;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentation;
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
//@TransactionConfiguration(defaultRollback = false)
public class ApplicationDocumentation {
	
	@Test
	public void applicationsListExample() throws Exception {
		this.mockMvc
			.perform(get("/applications"))
			.andExpect(status().isOk())
			.andDo(document("applications-list-example",
				responseFields(
					fieldWithPath("_links").description("<<resources-index-links,Links>> to other resources"),
					fieldWithPath("_embedded.applications").description("An array of <<resources-applications, Application resources>>"),
					fieldWithPath("page").description("Results pagination details")
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
	public void applicationsCreateExample() throws Exception {
		Application testApp = applicationRepository.save(testApplication());
		try{
			this.mockMvc
				.perform(post("/applications")
					.contentType(MediaTypes.HAL_JSON)
					.content(this.objectMapper.writeValueAsString(testApp))
				)
				.andExpect(status().isCreated())
				.andDo(document("applications-create-example",
					requestFields(
						fieldWithPath("id").description("The id code of the application"),
						fieldWithPath("name").description("The name of the application"),
						fieldWithPath("secret").description("The read/write secret of the application. Use it in combination with the application name as username."),
						fieldWithPath("readOnlySecret").description("The read only secret of the application. Use it in combination with the application name, with a suffix '.ro' as username."),
						fieldWithPath("products").description("Array of products, each with name and parameters")
					)
				));
		}finally{
			applicationRepository.delete(testApp);
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
