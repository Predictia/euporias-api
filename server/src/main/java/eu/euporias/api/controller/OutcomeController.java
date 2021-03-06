package eu.euporias.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Outcome;
import eu.euporias.api.model.ParameterValue;
import eu.euporias.api.model.Product;
import eu.euporias.api.repository.ApplicationRepository;
import eu.euporias.api.repository.OutcomeRepository;
import eu.euporias.api.repository.ProductRepository;

@RepositoryRestController
public class OutcomeController {

	public static final String REL = "parameters";
	
    @RequestMapping(value = "/outcomes/search/" + REL, method = RequestMethod.GET)
    @ResponseBody
    public PagedResources<PersistentEntityResource> findOutcomes(
    		@RequestParam(required=false) MultiValueMap<String, String> parameters, 
    		@RequestParam(required=false) Pageable pageable
    	) {
    	Page<Outcome> outcomePage = NO_OUTCOMES_PAGE;
    	ParameterHelper parameterHelper = new ParameterHelper(applicationRepository, productRepository, parameters);
    	if (parameterHelper.hasApplicationAndProduct()){
    		Application application = parameterHelper.application();
    		Product product = parameterHelper.product(application);
    		List<ParameterValue> pvs = parameterHelper.otherParameterValues();
        	if(pvs.isEmpty()){
        		outcomePage = outcomeRepository.findByApplicationAndProductOrderByLastModifiedDateDesc(application, product, pageable);
        	}else{
        		outcomePage = outcomeRepository.findOutcomes(application, product, pvs, pageable);
        	}
    	}    	
        return pagedResourcesAssembler.toResource(outcomePage);
    }
    
    private static final Page<Outcome> NO_OUTCOMES_PAGE = new PageImpl<>(new ArrayList<>());
    
    @Autowired private OutcomeRepository outcomeRepository;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private PagedResourcesAssembler pagedResourcesAssembler;
    
}
