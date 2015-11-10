package eu.euporias.api.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import eu.euporias.api.model.Product;

@RepositoryRestResource(exported=false)
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

}
