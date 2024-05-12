package com.example.discountcodesmanager.repository;

import com.example.discountcodesmanager.model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class ProductRepositoryTests {
    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void givenValidProduct_whenFindByName_thenReturnProduct() {
        // given
        Product product = new Product();
        product.setName("TestProduct");
        product.setDescription("This is a test product");
        product.setRegularPrice(new BigDecimal("29.99"));
        product.setCurrency(Currency.getInstance("USD"));
        productRepository.save(product);

        // when
        Optional<Product> found = productRepository.findByName(product.getName());

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getName()).isEqualTo(product.getName());
    }

    @Test
    public void givenInvalidProduct_whenFindByName_thenReturnEmpty() {
        // given
        String name = "NonExistentProduct";

        // when
        Optional<Product> found = productRepository.findByName(name);

        // then
        assertThat(found.isPresent()).isFalse();
    }
}
