package com.example.discountcodesmanager.controller;

import com.example.discountcodesmanager.DiscountCodesManagerApplication;
import com.example.discountcodesmanager.dto.ProductRequest;
import com.example.discountcodesmanager.exception.BadRequestException;
import com.example.discountcodesmanager.model.Product;
import com.example.discountcodesmanager.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DiscountCodesManagerApplication.class)
@AutoConfigureMockMvc
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        productRepository.deleteAll();
    }

    @Test
    public void givenProductRequest_whenCreateProduct_thenProductIsCreated() throws Exception {
        //given
        ProductRequest request = new ProductRequest();
        request.setName("MegaWidget");
        request.setDescription("A mega widget with super powers");
        request.setRegularPrice(new BigDecimal("79.99"));
        request.setCurrency("USD");

        //when
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))

                //then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.regularPrice", is(79.99)))
                .andExpect(jsonPath("$.currency", is(request.getCurrency())));
    }

    @Test
    public void givenExistingProducts_whenGetProducts_thenAllProductsReturned() throws Exception {
        //given
        Product product1 = new Product();
        product1.setName("SuperWidget");
        product1.setDescription("A top-quality widget for all your widget needs");
        product1.setRegularPrice(new BigDecimal("49.99"));
        product1.setCurrency(Currency.getInstance("USD"));
        productRepository.save(product1);

        //when
        mockMvc.perform(get("/products"))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("SuperWidget")));
    }

    @Test
    public void givenNoProducts_whenGetAllProducts_thenEmptyListIsReturned() throws Exception {
        //when
        mockMvc.perform(get("/products"))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void givenExistingProduct_whenCreateProduct_thenBadRequestResponse() throws Exception {
        //given
        Product product1 = new Product();
        product1.setName("SuperWidget");
        product1.setDescription("A top-quality widget for all your widget needs");
        product1.setRegularPrice(new BigDecimal("49.99"));
        product1.setCurrency(Currency.getInstance("USD"));
        productRepository.save(product1);

        ProductRequest request = new ProductRequest();
        request.setName(product1.getName());
        request.setDescription(product1.getDescription());
        request.setRegularPrice(product1.getRegularPrice());
        request.setCurrency(product1.getCurrency().toString());

        //when
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(result -> Assertions.assertEquals("Product with name: SuperWidget, already exists",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void givenInvalidProductRequest_whenCreateProduct_thenReturnError() throws Exception {
        // given
        ProductRequest request = new ProductRequest();
        request.setName("");
        request.setDescription("");
        request.setRegularPrice(null);
        request.setCurrency("USD");

        //when
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))

                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenValidProductId_whenPatchProduct_thenUpdateProduct() throws Exception {
        // Given
        Product product1 = new Product();
        product1.setName("SuperWidget");
        product1.setDescription("A top-quality widget for all your widget needs");
        product1.setRegularPrice(new BigDecimal("49.99"));
        product1.setCurrency(Currency.getInstance("USD"));
        productRepository.save(product1);
        String patchInJson = "{\"description\":\"Simple widget\"}";

        // When & Then
        mockMvc.perform(patch("/products/" + product1.getId())
                        .contentType("application/json")
                        .content(patchInJson))
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenInvalidProductId_whenPatchProduct_thenReturnError() throws Exception {
        // Given
        Product product1 = new Product();
        product1.setName("SuperWidget");
        product1.setDescription("A top-quality widget for all your widget needs");
        product1.setRegularPrice(new BigDecimal("49.99"));
        product1.setCurrency(Currency.getInstance("USD"));
        productRepository.save(product1);
        String patchInJson = "{\"description\":\"Simple widget\"}";

        // When & Then
        mockMvc.perform(patch("/products/" + product1.getId() + 1)
                        .contentType("application/json")
                        .content(patchInJson))
                .andExpect(status().isNotFound());
    }
}
