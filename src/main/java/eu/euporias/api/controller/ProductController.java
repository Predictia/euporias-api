package eu.euporias.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.euporias.api.model.Product;
import eu.euporias.api.service.ProductService;

//@RestController
public class ProductController {

    @RequestMapping("/products")
    public Iterable<Product> index() {
        return productService.getRepo().findAll();
    }
    
    @Autowired private ProductService productService;
    
}
