package eu.euporias.api.service;

import eu.euporias.api.model.Product;
import eu.euporias.api.repository.ProductRepository;

public interface ProductService extends CrateService<Product, ProductRepository, String> {

	public ProductRepository getRepo();
	
}