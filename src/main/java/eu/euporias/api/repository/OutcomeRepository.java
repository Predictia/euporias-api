package eu.euporias.api.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import eu.euporias.api.model.Outcome;

public interface OutcomeRepository extends PagingAndSortingRepository<Outcome, Long> {

}
