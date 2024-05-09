package com.example.discountcodesmanager.mapper;

import com.example.discountcodesmanager.dto.ProductRequest;
import com.example.discountcodesmanager.dto.ProductResponse;
import com.example.discountcodesmanager.model.Product;
import com.example.discountcodesmanager.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductMapper {
    private final CurrencyService currencyService;

    public Product mapFromRequest(ProductRequest request) {
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setCurrency(currencyService.getCurrency(request.getCurrency()).get());
        return product;
    }

    public static ProductResponse mapToResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product, productResponse);
        productResponse.setCurrency(product.getCurrency().toString());
        return productResponse;
    }
}
