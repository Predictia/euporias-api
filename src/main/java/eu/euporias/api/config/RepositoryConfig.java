package eu.euporias.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.euporias.api.repository.ApplicationEventHandler;
import eu.euporias.api.repository.OutcomeEventHandler;
import eu.euporias.api.repository.UserEventHandler;

@Configuration
public class RepositoryConfig {
	
	@Bean
	UserEventHandler userEventHandler() {
		return new UserEventHandler();
	}
	
	@Bean
	ApplicationEventHandler applicationEventHandler() {
		return new ApplicationEventHandler();
	}
	
	@Bean
	OutcomeEventHandler outcomeEventHandler() {
		return new OutcomeEventHandler();
	}

}
