package eu.euporias.api.repository;

import org.springframework.data.crate.repository.CrateRepository;

import eu.euporias.api.model.Product;

public interface ProductRepository extends CrateRepository<Product, String> {

}
