package com.example.discountcodesmanager.controller;

import com.example.discountcodesmanager.DiscountCodesManagerApplication;
import com.example.discountcodesmanager.model.DiscountType;
import com.example.discountcodesmanager.model.Product;
import com.example.discountcodesmanager.model.PromoCode;
import com.example.discountcodesmanager.repository.ProductRepository;
import com.example.discountcodesmanager.repository.PromoCodeRepository;
import com.example.discountcodesmanager.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DiscountCodesManagerApplication.class)
@AutoConfigureMockMvc
public class PurchaseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    PurchaseRepository purchaseRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PromoCodeRepository promoCodeRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        purchaseRepository.deleteAll();
        productRepository.deleteAll();
        promoCodeRepository.deleteAll();
    }

    @Test
    public void givenNonexistentProduct_whenCreatePurchase_thenNotFound() throws Exception {
        // given
        long productId = 999L;

        //when
        mockMvc.perform(post("/purchases")
                        .param("productId", Long.toString(productId)))

                //then
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenNonexistentPromoCode_whenCreatePurchase_thenNotFound() throws Exception {
        // given
        String promoCode = "INVALID";

        Product product1 = new Product();
        product1.setName("SuperWidget");
        product1.setDescription("A top-quality widget for all your widget needs");
        product1.setRegularPrice(new BigDecimal("49.99"));
        product1.setCurrency(Currency.getInstance("USD"));

        Product savedProduct = productRepository.save(product1);

        // when
        mockMvc.perform(post("/purchases")
                        .param("productId", savedProduct.getId().toString())
                        .param("promoCode", promoCode))

                // then
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenCurrencyMismatch_whenCalculateDiscount_thenCurrencyMismatchWarning() throws Exception {
        // given
        Product product1 = new Product();
        product1.setName("SuperWidget");
        product1.setDescription("A top-quality widget for all your widget needs");
        product1.setRegularPrice(new BigDecimal("49.99"));
        product1.setCurrency(Currency.getInstance("USD"));

        Product savedProduct = productRepository.save(product1);

        PromoCode promoCode = new PromoCode(
                DiscountType.FIXED,
                "PromoCode",
                LocalDateTime.now().plusDays(1),
                new BigDecimal("20.99"),
                Currency.getInstance("EUR"),
                5,
                1
        );

        promoCodeRepository.save(promoCode);

        //when
        mockMvc.perform(post("/purchases")
                        .param("productId", savedProduct.getId().toString())
                        .param("promoCode", promoCode.getCode()))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Warning: Currency mismatch"));
    }

    @Test
    public void givenMaxUsageExceeded_whenCreatePurchase_thenMaxUsageExceededWarning() throws Exception {
        //given
        Product product1 = new Product();
        product1.setName("SuperWidget");
        product1.setDescription("A top-quality widget for all your widget needs");
        product1.setRegularPrice(new BigDecimal("49.99"));
        product1.setCurrency(Currency.getInstance("USD"));

        Product savedProduct = productRepository.save(product1);

        PromoCode promoCode = new PromoCode(
                DiscountType.FIXED,
                "PromoCode",
                LocalDateTime.now().plusDays(1),
                new BigDecimal("20.99"),
                Currency.getInstance("USD"),
                5,
                5
        );

        promoCodeRepository.save(promoCode);

        //when
        mockMvc.perform(post("/purchases")
                        .param("productId", savedProduct.getId().toString())
                        .param("promoCode", promoCode.getCode()))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Warning: Promo code usage limit reached"));
    }

    @Test
    public void givenDiscountPriceGreaterThanProductPrice_whenCalculateDiscount_thenReturnZero() throws Exception {
        //given
        Product product1 = new Product();
        product1.setName("SuperWidget");
        product1.setDescription("A top-quality widget for all your widget needs");
        product1.setRegularPrice(new BigDecimal("49.99"));
        product1.setCurrency(Currency.getInstance("USD"));

        Product savedProduct = productRepository.save(product1);

        PromoCode promoCode = new PromoCode(
                DiscountType.FIXED,
                "PromoCode",
                LocalDateTime.now().plusDays(1),
                new BigDecimal("100.00"),
                Currency.getInstance("USD"),
                5,
                0
        );

        promoCodeRepository.save(promoCode);

        //when
        mockMvc.perform(get("/purchases/discount-price")
                        .param("productId", savedProduct.getId().toString())
                        .param("promoCode", promoCode.getCode()))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(0));
    }

    @Test
    public void givenExpiredPromoCode_whenCalculateDiscount_thenPromoCodeExpiredWarning() throws Exception {
        //given
        Product product1 = new Product();
        product1.setName("SuperWidget");
        product1.setDescription("A top-quality widget for all your widget needs");
        product1.setRegularPrice(new BigDecimal("49.99"));
        product1.setCurrency(Currency.getInstance("USD"));

        Product savedProduct = productRepository.save(product1);

        PromoCode promoCode = new PromoCode(
                DiscountType.FIXED,
                "PromoCode",
                LocalDateTime.now().minusDays(1),
                new BigDecimal("20.99"),
                Currency.getInstance("USD"),
                5,
                0
        );

        promoCodeRepository.save(promoCode);

        //when
        mockMvc.perform(post("/purchases")
                        .param("productId", savedProduct.getId().toString())
                        .param("promoCode", promoCode.getCode()))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Warning: Promo code has expired"));
    }

    @Test
    public void givenValidRequest_whenCreatePurchase_thenPurchaseIsCreated() throws Exception {
        //given
        Product product1 = new Product();
        product1.setName("SuperWidget");
        product1.setDescription("A top-quality widget for all your widget needs");
        product1.setRegularPrice(new BigDecimal("49.99"));
        product1.setCurrency(Currency.getInstance("USD"));

        Product savedProduct = productRepository.save(product1);

        PromoCode promoCode = new PromoCode(
                DiscountType.FIXED,
                "PromoCode",
                LocalDateTime.now().plusDays(1),
                new BigDecimal("20.99"),
                Currency.getInstance("USD"),
                5,
                0
        );

        promoCodeRepository.save(promoCode);

        //when
        mockMvc.perform(post("/purchases")
                        .param("productId", savedProduct.getId().toString())
                        .param("promoCode", promoCode.getCode()))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.product").value("SuperWidget"))
                .andExpect(jsonPath("$.discountApplied").value(20.99));
    }
}
