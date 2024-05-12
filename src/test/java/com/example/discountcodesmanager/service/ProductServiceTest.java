package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.dto.ProductRequest;
import com.example.discountcodesmanager.dto.ProductResponse;
import com.example.discountcodesmanager.exception.BadRequestException;
import com.example.discountcodesmanager.exception.ResourceNotFoundException;
import com.example.discountcodesmanager.mapper.ProductMapper;
import com.example.discountcodesmanager.model.Product;
import com.example.discountcodesmanager.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    private MockedStatic<ProductMapper> mockedProductMapper;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockedProductMapper = Mockito.mockStatic(ProductMapper.class);
    }

    @AfterEach
    public void tearDown() {
        mockedProductMapper.close();
    }

    @Test
    public void givenNewProduct_whenSaveProduct_thenProductIsSaved() {
        //given
        ProductRequest productRequest = new ProductRequest(
                "NewProduct",
                "Description",
                new BigDecimal(100),
                "USD");
        Product product = new Product(
                1L,
                "NewProduct",
                "Description",
                new BigDecimal(100),
                Currency.getInstance("USD"));
        ProductResponse productResponse = new ProductResponse(
                1L,
                "NewProduct",
                "Description",
                new BigDecimal(100),
                "USD");

        when(productRepository.findByName("NewProduct")).thenReturn(Optional.empty());
        when(productMapper.mapFromRequest(productRequest)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(ProductMapper.mapToResponse(product)).thenReturn(productResponse);

        //when
        ProductResponse savedProduct = productService.saveProduct(productRequest);

        //then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isEqualTo(1L);
        assertThat(savedProduct.getName()).isEqualTo("NewProduct");
        verify(productRepository).save(product);
    }

    @Test
    public void givenExistingProductName_whenSaveProduct_thenThrowBadRequestException() {
        //given
        ProductRequest productRequest = new ProductRequest(
                "ExistingProduct",
                "Description",
                new BigDecimal(100),
                "USD");

        when(productRepository.findByName("ExistingProduct")).thenReturn(Optional.of(new Product()));

        //when
        //then
        assertThatThrownBy(() -> productService.saveProduct(productRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    public void givenValidId_whenFindProductById_thenProductIsReturned() {
        //given
        Product product = new Product(
                1L,
                "ExistingProduct",
                "Description",
                new BigDecimal(100),
                Currency.getInstance("USD"));
        ProductResponse productResponse = new ProductResponse(
                1L,
                "ExistingProduct",
                "Description",
                new BigDecimal(100),
                "USD");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(ProductMapper.mapToResponse(product)).thenReturn(productResponse);

        //when
        Optional<ProductResponse> foundProduct = productService.findProductById(1L);

        //then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getId()).isEqualTo(1L);
        assertThat(foundProduct.get().getName()).isEqualTo("ExistingProduct");
    }

    @Test
    public void givenInvalidId_whenFindProductById_thenThrowResourceNotFoundException() {
        //given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> productService.findProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    public void givenValidProductRequest_whenUpdateProduct_thenProductIsUpdated() {
        //given
        ProductRequest productRequest = new ProductRequest(
                "UpdatedProduct",
                "Updated Description",
                new BigDecimal(150),
                "USD");
        Product existingProduct = new Product(
                1L,
                "UpdatedProduct",
                "Description",
                new BigDecimal(100),
                Currency.getInstance("USD"));
        Product updatedProduct = new Product(
                1L,
                "UpdatedProduct",
                "Updated Description",
                new BigDecimal(150),
                Currency.getInstance("USD"));

        when(productRepository.findByName("UpdatedProduct")).thenReturn(Optional.of(existingProduct));
        when(productMapper.mapFromRequest(productRequest)).thenReturn(updatedProduct);
        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);

        //when
        productService.updateProduct(productRequest);

        //then
        verify(productRepository).save(updatedProduct);
    }

    @Test
    public void givenNonexistentProductName_whenUpdateProduct_thenThrowResourceNotFoundException() {
        //given
        ProductRequest productRequest = new ProductRequest(
                "Nonexistent",
                "Description",
                new BigDecimal(100),
                "USD");

        when(productRepository.findByName("Nonexistent")).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> productService.updateProduct(productRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    public void givenProductsInDatabase_whenGetAllProducts_thenAllProductsReturned() {
        // given
        Product product1 = new Product(
                1L,
                "Product1",
                "Description1",
                new BigDecimal(100),
                Currency.getInstance("USD"));
        Product product2 = new Product(
                2L, "Product2",
                "Description2",
                new BigDecimal(200),
                Currency.getInstance("EUR"));
        List<Product> productList = Arrays.asList(product1, product2);
        ProductResponse productResponse1 = new ProductResponse(
                1L, "Product1",
                "Description1",
                new BigDecimal(100),
                "USD");
        ProductResponse productResponse2 = new ProductResponse(
                2L,
                "Product2",
                "Description2",
                new BigDecimal(200),
                "EUR");

        when(productRepository.findAll()).thenReturn(productList);
        mockedProductMapper.when(() -> ProductMapper.mapToResponse(any(Product.class)))
                .thenReturn(productResponse1, productResponse2);

        // when
        List<ProductResponse> productResponses = productService.getAllProducts();

        // then
        assertThat(productResponses).hasSize(2);
        assertThat(productResponses.get(0).getName()).isEqualTo("Product1");
        assertThat(productResponses.get(1).getName()).isEqualTo("Product2");
        verify(productRepository).findAll();
    }
}
