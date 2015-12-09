package eu.euporias.api.repository;

import static eu.euporias.api.util.Encoder.decodeBase64ToFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import eu.euporias.api.model.Feedback;
import eu.euporias.api.service.StorageService;

@RepositoryEventHandler(Feedback.class)
@Transactional
@Component
public class FeedbackEventHandler {

	@HandleBeforeCreate
	public void handleFeedbackCreate(Feedback feedback) throws IOException {
		feedback.setLastModifiedDate(new Date());
		Optional<String> attachment = Optional.ofNullable(feedback.getAttachment()).filter(s -> !s.isEmpty());
		if(attachment.isPresent()){
			try {
				File f = storageService.storeFile(decodeBase64ToFile(attachment.get()));
				feedback.setAttachment(storageService.relativeFilePath(f));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@HandleBeforeSave
	public void handleFeedbackSave(Feedback feedback) throws IOException {
		feedback.setLastModifiedDate(new Date());
	}
	
	@HandleBeforeDelete
	public void handleFeedbackDelete(Feedback feedback) {
		Optional<String> attachment = Optional.ofNullable(feedback.getAttachment()).filter(s -> !s.isEmpty());
		if(attachment.isPresent()){
			File f = storageService.expandFilePath(attachment.get());
			LOGGER.info("Deleting file {} related to feedback #{}", f, feedback.getId());
			f.delete();
		}
	}
	
	@Autowired private StorageService storageService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackEventHandler.class);

}
