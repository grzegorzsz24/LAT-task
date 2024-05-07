package com.example.discountcodesmanager.controller;

import com.example.discountcodesmanager.model.Product;
import com.example.discountcodesmanager.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) throws BadRequestException {
        return new ResponseEntity<>(productService.saveProduct(product), HttpStatus.CREATED);
    }
}
