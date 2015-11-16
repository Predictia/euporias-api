package eu.euporias.api.config;

import java.lang.reflect.Parameter;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;

import eu.euporias.api.model.OutcomeAccess;
import eu.euporias.api.model.RequestDetails;
import eu.euporias.api.repository.OutcomeAccessRepository;

@Configuration
@EnableJpaAuditing
@EnableAspectJAutoProxy
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
    
	@Bean
	public MethodInterceptor outcomeVisitInterceptor(final OutcomeAccessRepository outcomeAccessRepository) {
		return  new MethodInterceptor() {
			@Override
			public Object invoke(MethodInvocation invocation) throws Throwable {
				OutcomeAccess outcomeAccess = new OutcomeAccess();
				outcomeAccess.setMethodName(invocation.getMethod().getName());
				Parameter[] params = invocation.getMethod().getParameters();
				if(params != null){
					outcomeAccess.setArguments(StringUtils.arrayToCommaDelimitedString(invocation.getArguments()));
				}
				outcomeAccessRepository.save(outcomeAccess);
				return invocation.proceed();
			}
		};
	}

	@Bean
	public Advisor outcomeVisitAdvisor(OutcomeAccessRepository outcomeAccessRepository) {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(public * eu.euporias.api.repository.OutcomeRepository+.find*(..))");
		return new DefaultPointcutAdvisor(pointcut, outcomeVisitInterceptor(outcomeAccessRepository));
	}
    
    private static final Joiner COMMA_JOINER = Joiner.on(",").skipNulls();
    private static final MapJoiner MAP_JOINER = Joiner.on(";").useForNull("").withKeyValueSeparator("=");
	
}
