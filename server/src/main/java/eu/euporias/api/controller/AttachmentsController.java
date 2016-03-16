package eu.euporias.api.controller;

import static com.google.common.io.Files.getFileExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import eu.euporias.api.model.Outcome;
import eu.euporias.api.model.OutcomeType;
import eu.euporias.api.repository.OutcomeRepository;
import eu.euporias.api.service.StorageService;

@Controller
@Transactional
public class AttachmentsController {
	
	@RequestMapping(value = "/attachments/outcomes/{outcome}", method = RequestMethod.POST)
	public HttpEntity<?> saveFile(
			@PathVariable(value = "outcome") Long outcomeId, 
			@RequestPart("file") MultipartFile file
		) {
		Outcome outcome = outcomeRepository.findOne(outcomeId);
		if(!OutcomeType.FILE.equals(outcome.getOutcomeType())){
			LOGGER.info("Invalid outcome type requested provided");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if(outcome.getResults() == null){
			outcome.setResults(new ArrayList<>());
		}
		outcome.getResults().add(storeAndGetRelativePath(file));
		outcomeRepository.save(outcome);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	private String storeAndGetRelativePath(MultipartFile mpFile){
		try{
			String extension = getFileExtension(mpFile.getOriginalFilename());
			if(extension.isEmpty()){
				extension = "tmp";
			}
			File tmpFile = File.createTempFile("upload.-", "." + extension);
			tmpFile.deleteOnExit();
			mpFile.transferTo(tmpFile);
			File destFile = storageService.storeFile(tmpFile);
			return storageService.relativeFilePath(destFile);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	@RequestMapping(value = "/attachments/outcomes/{outcome}/{file}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
	@ResponseBody
	public FileSystemResource file(
			@PathVariable(value = "outcome") Long outcomeId, 
			@PathVariable(value = "file") Integer file,
			HttpServletResponse response
		) {
		Outcome outcome = outcomeRepository.findOne(outcomeId);
		for(int i = 0; i<outcome.getResults().size(); i++){
			if(Integer.valueOf(i).equals(file)){
				File expandedFile = storageService.expandFilePath(outcome.getResults().get(i));
				response.setHeader("Content-Disposition", "attachment; filename=outcome-" + outcome.getId() + "-" + i + "."  + getFileExtension(expandedFile.getAbsolutePath()));
				return new FileSystemResource(expandedFile);
			}
		}
		throw new IllegalArgumentException();
	}
	
	@RequestMapping(value = "/attachments/outcomes/{outcome}/{file}", method = RequestMethod.DELETE)
	@ResponseBody
	public HttpEntity<?> deleteFile(@PathVariable(value = "outcome") Long outcomeId, @PathVariable(value = "file") Integer file) {
		Outcome outcome = outcomeRepository.findOne(outcomeId);
		int removeIdx = -1;
		for(int i = 0; i<outcome.getResults().size(); i++){
			if(Integer.valueOf(i).equals(file)){
				File f = storageService.expandFilePath(outcome.getResults().get(i));
				f.delete();
				removeIdx = i;
			}
		}
		if(removeIdx >= 0){
			outcome.getResults().remove(removeIdx);
			outcomeRepository.save(outcome);
			return new ResponseEntity<>(HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@Autowired private OutcomeRepository outcomeRepository;
	@Autowired private StorageService storageService;

	private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentsController.class);

}
