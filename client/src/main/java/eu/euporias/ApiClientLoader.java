package eu.euporias;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import eu.euporias.ArgumentList.Argument;

@Component
public class ApiClientLoader implements CommandLineRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(ApiClientLoader.class);
	
	@Value("${apiUrl}")	private String apiUrl;
	@Value("${tokenUrl}") private String tokenUrl;
	@Value("${testTokenUrl}") private String testTokenUrl;
	@Value("${scope}") private String scope;
	@Value("${proxyUrl}") private String proxyUrl;
	@Value("${proxyPort}") private Integer proxyPort;

	@Override
	public void run(String... args) throws Exception {
		logger.info("Running API client with arguments: {}", Arrays.deepToString(args));
		ArgumentList arguments = ParamUtils.parameters(args,Predicates.not(ParamUtils.PARAMETER_PREDICATE));
		String action = arguments.get(Argument.action);
		RequestGenerator request = new RequestGenerator(config());
		
		String token = request.token(arguments);
		logger.info("Using token: "+token);
		
		if("search".equals(action)){
			String result = request.get("outcomes/search/parameters",token,arguments);
			logger.warn(result);
		}else if("delete".equals(action)){
			String result = request.delete("outcomes/",token,arguments);
			logger.warn(result);
		}else if("metadata".equals(action)){
			String result = request.get("outcomes/search/metadata",token,arguments);
			logger.warn(result);
		}else if("post".equals(action)){
			ArgumentList extraParameters = ParamUtils.parameters(args,ParamUtils.PARAMETER_PREDICATE);
			String result = request.post("outcomes/",token,arguments,extraParameters);
			logger.warn(result);
		}else if("put".equals(action)){
			ArgumentList allParameters = ParamUtils.parameters(args,new Predicate<String>(){
				@Override
				public boolean apply(String input) {
					if(input.contains("--mimeType")) return false;
					if(input.contains("--outcomeType")) return false;
					if(input.contains("--results")) return false;
					return true;
				}				
			});
			String element = request.get("outcomes/search/parameters",token,allParameters);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(element);
			String href = node.at("/_embedded/outcomes/0/_links/feedback/href").asText();			
			String id = href.split("=")[1];
			ArgumentList extraParameters = ParamUtils.parameters(args,ParamUtils.PARAMETER_PREDICATE);
			String result = request.put("outcomes/"+id,token,arguments,extraParameters);
			logger.warn(result);
		}else{
			throw new IllegalArgumentException("Invalid action "+action);
		}
	}
	
	private Map<String,String> config(){
		Map<String,String> config = new HashMap<String,String>();
		config.put("apiUrl",apiUrl);
		config.put("tokenUrl",tokenUrl);
		config.put("testTokenUrl",testTokenUrl);
		if(proxyPort!=null)	config.put("proxyPort",proxyPort.toString());
		config.put("proxyUrl",proxyUrl);
		return config;
	}	

}