package eu.euporias.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import eu.euporias.api.model.Feedback;
import eu.euporias.api.model.Outcome;

public interface FeedbackRepository extends PagingAndSortingRepository<Feedback, Long> {

	@RestResource(exported = false)
	@PreAuthorize("#oauth2.clientHasRole(#outcome.application.authority) or #oauth2.clientHasRole('ROLE_ADMIN')")
	public Page<Feedback> findByOutcomeOrderByLastModifiedDateDesc(@Param("outcome") Outcome outcome, Pageable page);

	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public Page<Feedback> findAll(Sort pageable);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	public Page<Feedback> findAll(Pageable pageable);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	void deleteAll();
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	<S extends Feedback> Iterable<S> save(Iterable<S> entities);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
	void delete(Iterable<? extends Feedback> entities);
	
	@Override
	@PreAuthorize("#oauth2.clientHasRole(returnObject.outcome.application.authority) or #oauth2.clientHasRole('ROLE_ADMIN')")
	public Feedback findOne(Long aLong);
	
	@Override
	@PreAuthorize("(#oauth2.clientHasRole(#feedback.outcome.application.authority) and #oauth2.hasScope('write')) or #oauth2.clientHasRole('ROLE_ADMIN')")
	public <S extends Feedback> S save(@P("feedback") S feedback);
	
	@Override
	@PreAuthorize("(#oauth2.clientHasRole(#feedback.outcome.application.authority) and #oauth2.hasScope('write')) or #oauth2.clientHasRole('ROLE_ADMIN')")
	public void delete(@P("feedback") Feedback feedback);
	
}
