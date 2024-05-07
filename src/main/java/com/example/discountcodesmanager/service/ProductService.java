package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.model.Product;
import com.example.discountcodesmanager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product saveProduct(Product product) throws BadRequestException {
        if (productRepository.findByName(product.getName()).isPresent()) {
                throw new BadRequestException("Product " + product.getName() + " already exists");
        }

        return productRepository.save(product);
    }
}
