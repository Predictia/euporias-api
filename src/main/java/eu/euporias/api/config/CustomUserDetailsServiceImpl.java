package eu.euporias.api.config;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import eu.euporias.api.model.Role;
import eu.euporias.api.model.User;
import eu.euporias.api.repository.RoleRepository;
import eu.euporias.api.repository.UserRepository;

@Transactional
class CustomUserDetailsServiceImpl implements UserDetailsService, InitializingBean {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);
		if(user == null){
			throw new UsernameNotFoundException("Unable to find user with name " + username);
		}else{
			user.getAuthorities();
			return user;
		}
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
		user.setPassword(passwordEncoder.encodePassword(defaultAdminPassword, null));
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
	

	@Autowired private UserRepository userRepository;
	@Autowired private RoleRepository roleRepository;
	@Autowired private PasswordEncoder passwordEncoder;

	@Value("${default.admin.password}") private String defaultAdminPassword;

}
