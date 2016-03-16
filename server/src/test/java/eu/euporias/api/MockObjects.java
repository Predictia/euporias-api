package eu.euporias.api;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Parameter;
import eu.euporias.api.model.ParameterType;
import eu.euporias.api.model.Product;

class MockObjects {

	private static final String TEST_APP_NAME = "TEST";
	
	public static Application testApplication(){
		Application testApp = new Application();
		testApp.setName(TEST_APP_NAME);
		Product product = new Product();
		product.setName("test reports");
		product.setParameters(Stream.of(
			parameter("reportName", "name of the report", ParameterType.TEXT, null)
		).collect(Collectors.toSet()));
		testApp.setProducts(Stream.of(product).collect(Collectors.toSet()));
		return testApp;
	}
	
	public static Parameter parameter(String name, String description, ParameterType type, String format){
		Parameter parameter = new Parameter();
		parameter.setName(name);
		parameter.setDescription(description);
		parameter.setParameterType(type);
		parameter.setFormat(format);
		return parameter;
	}
	
	public static ObjectNode testOutcome(Application testApp){
		ObjectNode json = JsonNodeFactory.instance.objectNode(); 
		json.set("application", JsonNodeFactory.instance.textNode("http://localhost:8080/applications/" + testApp.getId()));
		{
			ObjectNode product = JsonNodeFactory.instance.objectNode();
			product.set("name", JsonNodeFactory.instance.textNode(testApp.getProducts().iterator().next().getName()));
			json.set("product", product);
		}{
			ObjectNode parameters = JsonNodeFactory.instance.objectNode();
			parameters.set("reportName", JsonNodeFactory.instance.textNode("EUPORIAS PowerPoint Template"));
			json.set("parameters", parameters);
		}
		json.set("outcomeType", JsonNodeFactory.instance.textNode("FILE"));
		json.set("mimeType", JsonNodeFactory.instance.textNode("applicationapplication/vnd.openxmlformats-officedocument.presentationml.presentation"));
		return json;
	}
	
}
