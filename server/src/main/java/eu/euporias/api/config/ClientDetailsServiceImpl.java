package eu.euporias.api.config;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Role;
import eu.euporias.api.model.User;
import eu.euporias.api.repository.ApplicationRepository;
import eu.euporias.api.repository.RoleRepository;
import eu.euporias.api.repository.UserRepository;

/**
 * Creates {@link ClientDetails} from {@link User} objets and
 * {@link Application} objects. User objects are fetched using email as the
 * clientId field. They always have read/write scopes. Applications are mapped
 * using application name, or application name ending with
 * {@link ClientDetailsServiceImpl#READ_ONLY_SUFFIX} in case of read only
 * clients.
 * 
 * @author Max Tuni
 *
 */
@Service("clientDetailsService")
@Transactional
public class ClientDetailsServiceImpl implements ClientDetailsService, InitializingBean {

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
	
	@Autowired private ApplicationRepository applicationRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private RoleRepository roleRepository;

	@Value("${default.admin.password}") private String defaultAdminPassword;
	
}
