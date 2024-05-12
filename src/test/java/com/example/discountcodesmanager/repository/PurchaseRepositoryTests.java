package com.example.discountcodesmanager.repository;

import com.example.discountcodesmanager.model.DiscountType;
import com.example.discountcodesmanager.model.Product;
import com.example.discountcodesmanager.model.PromoCode;
import com.example.discountcodesmanager.model.Purchase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
public class PurchaseRepositoryTests {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PromoCodeRepository promoCodeRepository;

    @AfterEach
    void tearDown() {
        purchaseRepository.deleteAll();
    }

    @Test
    public void givenExistingPurchases_whenGenerateSalesReport_thenReturnSalesReportList() {
        // given
        Product product = createTestProduct("TestProduct1", "This is test product 1", new BigDecimal("50.00"), "USD");
        PromoCode promoCode = createPromoCode("Discount123", BigDecimal.valueOf(20), "USD");
        Purchase purchase1 = createPurchase(LocalDateTime.now(), new BigDecimal("50.00"), new BigDecimal("10.00"), product, promoCode);
        Purchase purchase2 = createPurchase(LocalDateTime.now(), new BigDecimal("100.00"), new BigDecimal("20.00"), product, promoCode);

        purchaseRepository.saveAll(List.of(purchase1, purchase2));

        // when
        List<Object[]> report = purchaseRepository.findSalesReportByCurrency();

        // then
        assertThat(report).hasSize(1);
        assertThat(report.get(0)[0]).isEqualTo(Currency.getInstance("USD"));
        assertThat(report.get(0)[1]).isEqualTo(new BigDecimal("150.00"));
        assertThat(report.get(0)[2]).isEqualTo(new BigDecimal("30.00"));
        assertThat(report.get(0)[3]).isEqualTo(2L);
    }

    @Test
    public void givenExistingDifferentCurrencyPurchases_whenGenerateSalesReport_thenReturnSalesReportList() {
        // given
        Product product1 = createTestProduct("TestProduct2", "This is test product 2", new BigDecimal("200.00"), "USD");
        Product product2 = createTestProduct("TestProduct3", "This is test product 3", new BigDecimal("300.00"), "EUR");

        PromoCode promoCode1 = createPromoCode("Discount123", BigDecimal.valueOf(20), "USD");
        PromoCode promoCode2 = createPromoCode("Discount1234", BigDecimal.valueOf(20), "EUR");

        Purchase purchase1 = createPurchase(LocalDateTime.now(), new BigDecimal("200.00"), new BigDecimal("25.00"), product1, promoCode1);
        Purchase purchase2 = createPurchase(LocalDateTime.now(), new BigDecimal("300.00"), new BigDecimal("30.00"), product2, promoCode2);

        purchaseRepository.saveAll(List.of(purchase1, purchase2));

        // when
        List<Object[]> report = purchaseRepository.findSalesReportByCurrency();

        // then
        assertThat(report).hasSize(2);
        Object[] usdReport = report.stream()
                .filter(r -> r[0].equals(Currency.getInstance("USD")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("USD report not found"));

        Object[] eurReport = report.stream()
                .filter(r -> r[0].equals(Currency.getInstance("EUR")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("EUR report not found"));

        assertThat(usdReport[0]).isEqualTo(Currency.getInstance("USD"));
        assertThat(usdReport[1]).isEqualTo(new BigDecimal("200.00"));
        assertThat(usdReport[2]).isEqualTo(new BigDecimal("25.00"));
        assertThat(usdReport[3]).isEqualTo(1L);

        assertThat(eurReport[0]).isEqualTo(Currency.getInstance("EUR"));
        assertThat(eurReport[1]).isEqualTo(new BigDecimal("300.00"));
        assertThat(eurReport[2]).isEqualTo(new BigDecimal("30.00"));
        assertThat(eurReport[3]).isEqualTo(1L);
    }

    private Product createTestProduct(String name, String description, BigDecimal price, String currencyCode) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setRegularPrice(price);
        product.setCurrency(Currency.getInstance(currencyCode));
        return productRepository.save(product);
    }

    private PromoCode createPromoCode(String code, BigDecimal discountValue, String currencyCode) {
        PromoCode promoCode = new PromoCode(
                DiscountType.FIXED,
                code,
                LocalDateTime.now().plusDays(1),
                discountValue,
                Currency.getInstance(currencyCode),
                5,
                1
        );
        return promoCodeRepository.save(promoCode);
    }

    private Purchase createPurchase(LocalDateTime dateTime, BigDecimal total, BigDecimal discount, Product product, PromoCode promoCode) {
        return new Purchase(dateTime, total, discount, product, promoCode);
    }
}
