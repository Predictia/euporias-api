package eu.euporias.api.repository;

import javax.validation.ValidationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import eu.euporias.api.ApiApplication;
import eu.euporias.api.model.User;
import eu.euporias.api.repository.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebAppConfiguration
@DirtiesContext
public class UserRepositoryTest {

	@Test
	public void testSaveUser() throws Exception{
		User u = userRepository.save(createTestUser());
		try{
			Assert.assertNotNull(u.getEmail());
			Assert.assertNotNull(u.getFirstName());
		}finally{
			userRepository.delete(u);
		}
	}
	
	@Test
	public void testFindUserByEmail() throws Exception{
		String email = "test@user.net";
		User u = userRepository.save(createTestUser(email));
		try{
			User oUser = userRepository.findByEmail(email);
			Assert.assertEquals(email, oUser.getEmail());
		}finally{
			userRepository.delete(u);
		}
	}
	
	@Test(expected=DataIntegrityViolationException.class)
	public void testUniqueEmailUser() throws Exception{
		User u = userRepository.save(createTestUser());
		try{
			User u2 = userRepository.save(createTestUser());
			userRepository.delete(u2);
		}finally{
			userRepository.delete(u);
		}
	}
	
	@Test(expected=ValidationException.class)
	public void testInvalidEmailUser() throws Exception{
		User u = userRepository.save(createTestUser("kk"));
		userRepository.delete(u);
	}
	
	private User createTestUser(){
		return createTestUser("test@user.net");
	}
	
	private User createTestUser(String email){
		User user = new User();
		user.setFullName("test", "user");
		user.setEmail(email);
		return user;
	}
	
	@Autowired private UserRepository userRepository;
	
}
