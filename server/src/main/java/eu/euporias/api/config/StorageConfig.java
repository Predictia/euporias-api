package eu.euporias.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.euporias.api.service.StorageService;
import eu.euporias.api.service.StorageServiceImpl;

@Configuration
public class StorageConfig {
	
	@Bean
	public StorageService storageService() throws Exception {
		StorageServiceImpl ssi = new StorageServiceImpl();
		ssi.setBaseFolder(baseFolderParameter);
		ssi.init();
		return ssi;
	}

	private @Value("${storage.base.folder}") String baseFolderParameter;
	
}
