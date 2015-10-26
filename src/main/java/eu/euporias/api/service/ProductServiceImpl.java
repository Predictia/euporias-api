package eu.euporias.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.euporias.api.model.Product;
import eu.euporias.api.repository.ProductRepository;

@Service("productService")
public class ProductServiceImpl extends AbstractService<Product, Long, ProductRepository> implements ProductService {

	@Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
		super(Product.class, productRepository);
	}

}

