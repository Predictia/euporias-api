package eu.euporias.api.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import eu.euporias.api.model.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

}
