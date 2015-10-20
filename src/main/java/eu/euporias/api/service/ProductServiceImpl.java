package eu.euporias.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.euporias.api.model.Product;
import eu.euporias.api.repository.ProductRepository;

@Service("productService")
public class ProductServiceImpl extends AbstractCrateService<Product, ProductRepository, String> implements ProductService {

	@Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
		super(Product.class, productRepository);
	}

}

