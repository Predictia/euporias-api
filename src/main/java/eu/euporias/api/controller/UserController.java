package eu.euporias.api.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.euporias.api.model.User;
import eu.euporias.api.service.UserService;

//@RestController
public class UserController {

    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_PAGE_SIZE = "20";
    
    @RequestMapping(value="/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HttpEntity<PagedResources<Resource<User>>> index(
    		@RequestParam(value="page", required=false, defaultValue=DEFAULT_PAGE) Integer page,
    		@RequestParam(value="size", required=false, defaultValue=DEFAULT_PAGE_SIZE) Integer size
    	) {
        Page<User> users = userService.getRepo().findAll(new PageRequest(page, size));
        PagedResources<Resource<User>> usersResources = assembler.toResource(users);
        return new ResponseEntity<>(usersResources, HttpStatus.OK);
    }
    
    @RequestMapping(value="/user/{email}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HttpEntity<UserResource> user(@PathVariable(value = "email") String email) {
    	User user = userService.getRepo().findByEmail(email);
    	UserResource resource = new UserResource(user);
    	resource.add(linkTo(methodOn(UserController.class).user(email)).withSelfRel());
    	resource.add(linkTo(methodOn(UserController.class).index(null, null)).withRel("users"));
        return new ResponseEntity<>(
        	resource, 
        	user == null ? HttpStatus.NOT_FOUND : HttpStatus.OK
        );
    }
    
    @RequestMapping(value="/user", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HttpEntity<?> user(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
        	User pUser = userService.getRepo().save(user);
        	return user(pUser.getEmail());
        }else{
        	LOGGER.info("Invalid user requested: {}", bindingResult.getFieldError());
        	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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

    @Autowired private PagedResourcesAssembler<User> assembler;
    @Autowired private UserService userService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    
}
