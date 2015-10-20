package eu.euporias.api.repository;

import org.springframework.data.crate.repository.CrateRepository;

import eu.euporias.api.model.Outcome;

public interface OutcomeRepository extends CrateRepository<Outcome, String> {

}
