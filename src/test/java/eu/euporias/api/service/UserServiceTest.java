package eu.euporias.api.service;

import javax.validation.ValidationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;

import eu.euporias.api.ApiApplication;
import eu.euporias.api.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebAppConfiguration
@DirtiesContext
public class UserServiceTest {

	@Test
	public void testSaveUser() throws Exception{
		User u = userService.getRepo().save(createTestUser());
		try{
			Assert.assertEquals(testPrototypeName, u.getPrototypes()[0]);
			Assert.assertNotNull(u.getEmail());
			Assert.assertNotNull(u.getFirstName());
		}finally{
			userService.getRepo().delete(u);
		}
	}
	
	@Test
	public void testFindUserByEmail() throws Exception{
		User u = userService.getRepo().save(createTestUser());
		try{
			Assert.assertNotNull(userService.getRepo().findByEmail(u.getEmail()));
		}finally{
			userService.getRepo().delete(u);
		}
	}
	
	@Test(expected=ORecordDuplicatedException.class)
	public void testUniqueEmailUser() throws Exception{
		User u = userService.getRepo().save(createTestUser());
		try{
			User u2 = userService.getRepo().save(createTestUser());
			userService.getRepo().delete(u2);
		}finally{
			userService.getRepo().delete(u);
		}
	}
	
	@Test(expected=ValidationException.class)
	public void testInvalidEmailUser() throws Exception{
		User u = userService.getRepo().save(createTestUser("kk"));
		userService.getRepo().delete(u);
	}
	
	private User createTestUser(){
		return createTestUser("test@user.net");
	}
	
	private User createTestUser(String email){
		User user = new User();
		user.setFullName("test", "user");
		user.setEmail(email);
		user.setPrototypes(new String[]{testPrototypeName});
		return user;
	}
	
	private static final String testPrototypeName = "testPrototype";
	
	@Autowired private UserService userService;
	
}
