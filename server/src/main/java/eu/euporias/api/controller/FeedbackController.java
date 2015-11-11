package eu.euporias.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.euporias.api.model.Feedback;
import eu.euporias.api.model.Outcome;
import eu.euporias.api.repository.FeedbackRepository;
import eu.euporias.api.repository.OutcomeRepository;

@RepositoryRestController
public class FeedbackController {

	public static final String REL = "feedbacks";
	
	@RequestMapping(value = "/outcomes/search/" + REL, method = RequestMethod.GET)
    @ResponseBody
    public PagedResources<PersistentEntityResource> findFeedback(
    		@RequestParam("outcome") Long outcomeId, 
    		@RequestParam(required=false) Pageable pageable
    	) {
		Outcome outcome = outcomeRepository.findOne(outcomeId);
		Page<Feedback> feedbacksPage = feedbackRepository.findByOutcomeOrderByLastModifiedDateDesc(outcome, pageable);
    	return pagedResourcesAssembler.toResource(feedbacksPage);
    }
    
	@Autowired private OutcomeRepository outcomeRepository;
    @Autowired private FeedbackRepository feedbackRepository;
    @Autowired private PagedResourcesAssembler pagedResourcesAssembler;
    
}
