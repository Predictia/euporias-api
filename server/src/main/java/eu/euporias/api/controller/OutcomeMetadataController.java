package eu.euporias.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Product;
import eu.euporias.api.repository.ApplicationRepository;
import eu.euporias.api.repository.ProductRepository;
import eu.euporias.api.service.OutcomeMetadataService;

@RepositoryRestController
public class OutcomeMetadataController {

	public static final String REL = "metadata";
	
	public static final String PARAMETER_NAME_KEY = "parameter";
	
    @RequestMapping(value = "/outcomes/search/" + REL, method = RequestMethod.GET)
    @ResponseBody
    public PagedResources<Resource<String>> findOutcomes(
    		@RequestParam(required=false) MultiValueMap<String, String> parameters, 
    		@RequestParam(required=false) Pageable pageable
    	) {
    	if(pageable == null){
    		pageable = new PageRequest(0, 20);
    	}
    	Page<String> responsePage = EMPTY_PAGE;
    	ParameterHelper parameterHelper = new ParameterHelper(applicationRepository, productRepository, parameters);
    	if (parameterHelper.hasApplicationAndProduct() && parameters.containsKey(PARAMETER_NAME_KEY)){
    		Application application = parameterHelper.application();
    		Product product = parameterHelper.product(application);
    		String parameterName = parameters.getFirst(PARAMETER_NAME_KEY);
    		Integer total = outcomeMetadataService.countByParameterName(application, product, parameterName);
    		if(total > 0){
    			List<String> pageContent = outcomeMetadataService.findByParameterName(application, product, parameterName, pageable);
    			responsePage = new PageImpl<>(pageContent, pageable, total.longValue());
    		}
    	}	
        return pagedResourcesAssembler.toResource(responsePage);
    }
    
    private static final Page<String> EMPTY_PAGE = new PageImpl<>(new ArrayList<>());
    
    @Autowired private OutcomeMetadataService outcomeMetadataService;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private PagedResourcesAssembler pagedResourcesAssembler;
    
}
