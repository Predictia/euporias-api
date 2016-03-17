package eu.euporias.api;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.RequestDispatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebAppConfiguration
public class ApiDocumentation {
	
	@Test
	public void indexExample() throws Exception {
		this.mockMvc.perform(get("/"))
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
	
	@Test
	public void errorExample() throws Exception {
		this.mockMvc
			.perform(get("/error")
				.requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 400)
				.requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/applications")
				.requestAttr(RequestDispatcher.ERROR_MESSAGE, "The tag 'http://localhost:8080/applications/123' does not exist"))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(jsonPath("error", is("Bad Request")))
				.andExpect(jsonPath("timestamp", is(notNullValue())))
				.andExpect(jsonPath("status", is(400)))
				.andExpect(jsonPath("path", is(notNullValue())))
				.andDo(document("error-example",
					responseFields(
						fieldWithPath("error").description("The HTTP error that occurred, e.g. `Bad Request`"),
						fieldWithPath("message").description("A description of the cause of the error"),
						fieldWithPath("path").description("The path to which the request was made"),
						fieldWithPath("status").description("The HTTP status code, e.g. `400`"),
						fieldWithPath("timestamp").description("The time, in milliseconds, at which the error occurred")
					)
				));
	}
	
	private MockMvc mockMvc;
	
	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(
				documentationConfiguration(this.restDocumentation)
			).build();
	}

	@Rule
	public final RestDocumentation restDocumentation = new RestDocumentation("target/generated-snippets");
	
	@Autowired private ObjectMapper objectMapper;
	@Autowired private WebApplicationContext context;

}
