package eu.euporias.api.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.euporias.api.model.User;
import eu.euporias.api.service.UserService;

@RestController
public class UserController {

    @RequestMapping("/user")
    public Collection<User> index() {
        userService.refresh();
        return userService.getRepo().findAll();
    }
    
    @Autowired private UserService userService;
    
}
