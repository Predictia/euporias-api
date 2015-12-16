package eu.euporias;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.fge.jackson.JsonLoader;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import eu.euporias.ArgumentList.Argument;

public class RequestGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(RequestGenerator.class);
	
	private static final String TOKEN_FILE = "euporias-api-token.txt";
	private static final String JSON_DEFINITION = "application/json";
	private final Map<String,String> config;
	
	RequestGenerator(Map<String,String> config){
		this.config = config;
	}
	
	protected String token(ArgumentList parameters) throws IOException{
		File tokenFile = new File(System.getProperty("java.io.tmpdir"),TOKEN_FILE);
		Boolean requestNewToken = true;
		
		String token = "";
		if(tokenFile.exists()){
			token = Files.readFirstLine(tokenFile, Charsets.UTF_8);
			logger.info("Testing the existing token "+token);
			requestNewToken = !isValidToken(token,parameters);
			logger.info("The token is "+(requestNewToken?"not":"")+" valid");
		}		
		if(requestNewToken){
			token = newToken(parameters);
			Files.write(token, tokenFile, Charsets.UTF_8);
		}
		return token;
	}
	
	protected String get(String action,String token,ArgumentList parameters) throws UnsupportedOperationException, IOException{
		HttpClient client = HttpClientBuilder.create().build();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(Map.Entry<Argument,String> param : parameters.get()){
			params.add(new BasicNameValuePair(param.getKey().name(),param.getValue()));
		}		
		HttpGet get = new HttpGet(config.get("apiUrl")+ action+"?"+URLEncodedUtils.format(params, "UTF-8"));
		get.setHeader("Authorization", "Bearer "+token);
		get.setHeader("Content-Type", JSON_DEFINITION);		
		HttpResponse response = client.execute(get);
		return IOUtils.toString(response.getEntity().getContent(), "UTF-8");
	}
	
	protected String post(String action,String token,ArgumentList parameters,ArgumentList extraParameters) throws UnsupportedOperationException, IOException{
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(config.get("apiUrl") + action);
		StringEntity postString = new StringEntity(jsonPost(parameters,extraParameters));
		post.setEntity(postString);
		post.setHeader("Authorization", "Bearer "+token);
		post.setHeader("Content-type", JSON_DEFINITION);
		HttpResponse response = client.execute(post);
		return IOUtils.toString(response.getEntity().getContent(), "UTF-8");
	}
	
	private String jsonPost(ArgumentList parameters,ArgumentList extraParameters){
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("\"application\":\""+config.get("apiUrl")+"applications/"+parameters.get(Argument.application)+"\",\n");
		builder.append("\"product\":{\"name\":\""+parameters.get(Argument.product)+"\"},\n");
		builder.append("\"outcomeType\":\""+parameters.get(Argument.outcomeType)+"\",\n");
		builder.append("\"mimeType\":\""+parameters.get(Argument.mimeType)+"\",\n");
		builder.append("\"results\":[\""+parameters.get(Argument.results)+"\"],\n");
		builder.append("\"parameters\":{\n");
		boolean first = true;
		for(Entry<eu.euporias.ArgumentList.Argument, String> extra : extraParameters.get()){
			if(!first) builder.append(",");
			builder.append("\""+extra.getKey()+"\":\""+extra.getValue()+"\"\n");
			first = false;
		}		
		builder.append("}\n");
		builder.append("}");
		return builder.toString();
	}
	
	private Boolean isValidToken(String token,ArgumentList parameters) throws UnsupportedOperationException, IOException{
		String result = get(config.get("testTokenUrl"),token,parameters);
		return JsonLoader.fromString(result).get("error")==null;
	}
	
	private String newToken(ArgumentList parameters) throws IOException{
		logger.info("Requesting a new token");
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost post = new HttpPost(config.get("apiUrl") + config.get("tokenUrl"));
		String authorization = BasicAuthHelper.createHeader(parameters.get(Argument.user),parameters.get(Argument.secret));

		post.setHeader("Authorization",authorization);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("grant_type", "client_credentials"));
		params.add(new BasicNameValuePair("scope", config.get("scope")));
		post.setEntity(new UrlEncodedFormEntity(params));

		HttpResponse response = client.execute(post);
		String result = IOUtils.toString(response.getEntity().getContent(), "UTF-8");

		return JsonLoader.fromString(result).get("access_token").textValue();
	}
}