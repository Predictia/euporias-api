package eu.euporias.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.euporias.api.model.Outcome;
import eu.euporias.api.service.OutcomeService;

//@RestController
public class OutcomeController {

    @RequestMapping("/outcomes")
    public Iterable<Outcome> index() {
        return outcomeService.getRepo().findAll();
    }
    
    @Autowired private OutcomeService outcomeService;
    
}
