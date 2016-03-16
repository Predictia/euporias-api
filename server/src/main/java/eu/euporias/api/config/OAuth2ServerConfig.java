package eu.euporias.api.config;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.transaction.annotation.Transactional;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Role;
import eu.euporias.api.model.User;
import eu.euporias.api.repository.ApplicationRepository;
import eu.euporias.api.repository.RoleRepository;
import eu.euporias.api.repository.UserRepository;

@Configuration
@EnableAuthorizationServer
@Transactional
public class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter implements ClientDetailsService, InitializingBean {
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager);
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients
			.withClientDetails(this);
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		try{
			return findApplicationClient(clientId);
		}catch(ClientRegistrationException e){
			User user = userRepository.findByEmail(clientId);
			if(user != null){
				return clientDetailsFromUser(user);
			}else{
				throw e;
			}
		}
	}
	
	public static final String READ_ONLY_SUFFIX = ".ro";
	
	private ClientDetails findApplicationClient(String clientId){
		boolean roClient = clientId.endsWith(READ_ONLY_SUFFIX);
		String applicationName = roClient ?
			clientId.substring(0, clientId.lastIndexOf(READ_ONLY_SUFFIX)) :
			clientId;
		Application application = applicationRepository.findByName(applicationName);
		if(application == null){
			throw new ClientRegistrationException("Unable to find client with id " + clientId);
		}
		BaseClientDetails clientDetails = new BaseClientDetails(
			clientId, 
			ResourceServerConfig.RESOURCE_ID, 
			roClient ? "read" : "read,write", 
			"client_credentials", 
			application.getAuthority()
		);
		clientDetails.setClientSecret(roClient ? 
			application.getReadOnlySecret() : 
			application.getSecret()
		);
		return clientDetails;
	}

	private BaseClientDetails clientDetailsFromUser(User user){
		BaseClientDetails clientDetails = new BaseClientDetails(
			user.getEmail(), 
			ResourceServerConfig.RESOURCE_ID, 
			"read,write", 
			"client_credentials", 
			user.getAuthorities().stream()
				.map(a -> a.getAuthority())
				.collect(Collectors.joining(","))
			);
		clientDetails.setClientSecret(user.getPassword());
		return clientDetails;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(userRepository.count() == 0l){
			userRepository.save(createDefaultUser());
		}
	}
	
	private User createDefaultUser() throws Exception{
		User user = new User();
		user.setEmail("admin@api.euporias.eu");
		user.setDateJoined(new Date());
		user.setDisabled(Boolean.FALSE);
		user.setPassword(defaultAdminPassword);
		user.setRoles(Stream.of(findOrCreateRole("ADMIN")).collect(Collectors.toSet()));
		return user;
	}

	private Role findOrCreateRole(String roleName){
		if(roleRepository.findByName(roleName) != null){
			return roleRepository.findByName(roleName);
		}else{
			Role role = new Role();
			role.setName(roleName);
			return roleRepository.save(role);
		}
	}
	
	@Value("${default.admin.password}") private String defaultAdminPassword;
	
	@Autowired private ApplicationRepository applicationRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private RoleRepository roleRepository;
	@Autowired private AuthenticationManager authenticationManager;
	
}
