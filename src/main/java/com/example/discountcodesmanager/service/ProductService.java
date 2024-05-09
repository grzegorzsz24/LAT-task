package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.dto.ProductRequest;
import com.example.discountcodesmanager.exception.BadRequestException;
import com.example.discountcodesmanager.exception.ResourceNotFoundException;
import com.example.discountcodesmanager.mapper.ProductRequestMapper;
import com.example.discountcodesmanager.model.Product;
import com.example.discountcodesmanager.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final ProductRequestMapper productMapper;

    public Product saveProduct(ProductRequest product) {
        if (productRepository.findByName(product.getName()).isPresent()) {
            throw new BadRequestException("Product with name: " + product.getName() + ", already exists");
        }

        return productRepository.save(productMapper.map(product));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findProductById(Long id) {
        return Optional.ofNullable(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id: " + id + ", not found")));
    }

    public void updateProduct(Product productToUpdate) {
        productRepository.save(productToUpdate);
    }

    public Product applyPatch(Product product, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode commentNode = objectMapper.valueToTree(product);
        JsonNode commentPatchedNode = patch.apply(commentNode);
        return objectMapper.treeToValue(commentPatchedNode, Product.class);
    }
}
