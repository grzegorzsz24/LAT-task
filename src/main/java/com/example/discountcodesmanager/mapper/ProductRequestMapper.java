package com.example.discountcodesmanager.mapper;

import com.example.discountcodesmanager.dto.ProductRequest;
import com.example.discountcodesmanager.model.Product;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ProductRequestMapper {
    Product product = new Product();

    public Product map(ProductRequest request) {
        BeanUtils.copyProperties(request, product);
        return product;
    }
}
