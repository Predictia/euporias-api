package eu.euporias;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ApiClientApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
	    	.showBanner(true)
	    	.sources(ApiClientApplication.class)
	    	.run(args);
    }
}
