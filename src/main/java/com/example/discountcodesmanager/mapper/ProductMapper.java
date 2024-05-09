package com.example.discountcodesmanager.mapper;

import com.example.discountcodesmanager.dto.ProductRequest;
import com.example.discountcodesmanager.dto.ProductResponse;
import com.example.discountcodesmanager.model.Product;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ProductMapper {
    public Product mapFromRequest(ProductRequest request) {
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        return product;
    }

    public static ProductResponse mapToResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product, productResponse);
        return productResponse;
    }

}
