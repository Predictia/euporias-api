package eu.euporias.api.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import eu.euporias.api.ApiApplication;
import eu.euporias.api.model.Application;
import eu.euporias.api.model.Outcome;
import eu.euporias.api.model.ParameterValue;
import eu.euporias.api.model.Product;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebAppConfiguration
@DirtiesContext
@Transactional
public class OutcomeRepositoryTest {

	@Ignore("depends on exising outcome")
	@Test
	public void testSearchRepo() throws Exception{
		Application application = StreamSupport
			.stream(applicationRepository.findAll().spliterator(), false)
			.findFirst().get();
		Product product = application.getProducts().stream().findFirst().get();
		List<ParameterValue> params = new ArrayList<>();
		params.add(new ParameterValue("forecastStartTime", "2015102700Z"));
		params.add(new ParameterValue("stationId", "00003544"));
		Page<Outcome> page = outcomeRepository.findOutcomesByParameters(application, product, params, new PageRequest(0, 20));
		Assert.assertFalse(page.getContent().isEmpty());
	}
	
	@Autowired private ApplicationRepository applicationRepository;
	@Autowired private ProductRepository productRepository;
	@Autowired private OutcomeRepository outcomeRepository;
	
}
