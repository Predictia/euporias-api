package eu.euporias.api.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.euporias.api.model.User;
import eu.euporias.api.service.UserService;

@RestController
public class UserController {

    @RequestMapping("/users")
    public Collection<User> index() {
        userService.refresh();
        return userService.getRepo().findAll();
    }
    
    
    @RequestMapping(value="/user/{email}", method=RequestMethod.GET)
    @ResponseBody
    public HttpEntity<UserResource> user(@PathVariable(value = "email") String email) {
    	User user = userService.getRepo().findOne(email);
    	UserResource resource = new UserResource(user);
    	resource.add(linkTo(methodOn(UserController.class).user(email)).withSelfRel());
    	resource.add(linkTo(methodOn(UserController.class).index()).withRel("users"));
        return new ResponseEntity<>(
        	resource, 
        	user == null ? HttpStatus.NOT_FOUND : HttpStatus.OK
        );
    }

    @SuppressWarnings("unused")
    private static class UserResource extends ResourceSupport {
    	
    	private final User user;
    	
    	public UserResource(User user) {
			super();
			this.user = user;
		}
		
    	public User getUser() {
			return user;
		}
    	
    }
    
    @Autowired private UserService userService;
    
}
