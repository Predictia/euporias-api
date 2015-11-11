package eu.euporias.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{

	public static String RESOURCE_ID = "RESOURCE";
	
    @Override 
    public void configure(HttpSecurity http) throws Exception {
         http
         	.authorizeRequests().anyRequest().access("#oauth2.hasScope('read')")
         	.and().logout().invalidateHttpSession(true);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    	resources.resourceId(RESOURCE_ID);
    }

}