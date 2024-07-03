package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.dto.ProductRequest;
import com.example.discountcodesmanager.dto.ProductResponse;
import com.example.discountcodesmanager.exception.BadRequestException;
import com.example.discountcodesmanager.exception.ResourceNotFoundException;
import com.example.discountcodesmanager.mapper.ProductMapper;
import com.example.discountcodesmanager.model.Product;
import com.example.discountcodesmanager.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse saveProduct(ProductRequest product) {
        if (productRepository.findByName(product.getName()).isPresent()) {
            throw new BadRequestException("Product with name: " + product.getName() + ", already exists");
        }

        return ProductMapper.mapToResponse(productRepository.save(productMapper.mapFromRequest(product)));
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::mapToResponse)
                .toList();
    }

    public Optional<ProductResponse> findProductById(Long id) {
        return Optional.ofNullable(productRepository.findById(id)
                .map(ProductMapper::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id: " + id + ", not found"))
        );
    }

    @Transactional
    public void updateProduct(ProductRequest productToUpdate, String oldName) {
        Product product = productMapper.mapFromRequest(productToUpdate);
        product.setId(productRepository.findByName(oldName)
                .orElseThrow(() -> new ResourceNotFoundException("Product with name: " + productToUpdate.getName() + ", not found"))
                .getId());
        productRepository.save(product);
    }

    public void patchAndUpdateProduct(Long id, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException, ResourceNotFoundException {
        ProductResponse productResponse = findProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id: " + id + ", not found"));
        ProductRequest patchedProductRequest = applyPatch(productResponse, patch);
        updateProduct(patchedProductRequest, productResponse.getName());
    }

    private ProductRequest applyPatch(ProductResponse product, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode productNode = objectMapper.valueToTree(product);
        JsonNode patchedProductNode = patch.apply(productNode);
        return objectMapper.treeToValue(patchedProductNode, ProductRequest.class);
    }
}
