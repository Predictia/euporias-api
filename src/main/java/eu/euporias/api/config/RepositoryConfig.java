package eu.euporias.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.euporias.api.repository.OutcomeEventHandler;

@Configuration
public class RepositoryConfig {

	@Bean
	OutcomeEventHandler outcomeEventHandler() {
		return new OutcomeEventHandler();
	}

}
