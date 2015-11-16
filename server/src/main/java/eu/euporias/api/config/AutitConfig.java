package eu.euporias.api.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;

import eu.euporias.api.model.RequestDetails;

@Configuration
@EnableJpaAuditing
public class AutitConfig {

	@Bean
    public AuditorAware<RequestDetails> createAuditorProvider() {
        return new SecurityAuditor();
    }

    @Bean
    public AuditingEntityListener createAuditingListener() {
        return new AuditingEntityListener();
    }

    public static class SecurityAuditor implements AuditorAware<RequestDetails> {
        @Override
        public RequestDetails getCurrentAuditor() {
        	RequestDetails clientDetails = new RequestDetails();
        	ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        	if(requestAttributes != null){
        		clientDetails.setRemoteAddress(requestAttributes.getRequest().getRemoteAddr());
        		clientDetails.setUserAgent(requestAttributes.getRequest().getHeader("User-Agent"));	
        	}
        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        	if(auth != null){
        		clientDetails.setAuthName(auth.getName());
        	}
        	if(auth instanceof OAuth2Authentication){
        		Map<String, String> oAuth2RequestParameters = ((OAuth2Authentication) auth).getOAuth2Request().getRequestParameters();
        		clientDetails.setoAuth2RequestParameters(MAP_JOINER.join(oAuth2RequestParameters));
        		clientDetails.setoAuth2Scope(COMMA_JOINER.join(((OAuth2Authentication) auth).getOAuth2Request().getScope()));
            }
        	return clientDetails;
        }
    }
    
    private static final Joiner COMMA_JOINER = Joiner.on(",").skipNulls();
    private static final MapJoiner MAP_JOINER = Joiner.on(";").useForNull("").withKeyValueSeparator("=");
	
}
