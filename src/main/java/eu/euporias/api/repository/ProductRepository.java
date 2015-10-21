package eu.euporias.api.repository;

import org.springframework.data.orient.object.repository.OrientObjectRepository;

import eu.euporias.api.model.Product;

public interface ProductRepository extends OrientObjectRepository<Product> {

}
