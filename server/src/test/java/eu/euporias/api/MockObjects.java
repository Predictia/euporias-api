package eu.euporias.api;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	
}
