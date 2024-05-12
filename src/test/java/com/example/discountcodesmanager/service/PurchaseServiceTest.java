package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.dto.DiscountResponse;
import com.example.discountcodesmanager.exception.ResourceNotFoundException;
import com.example.discountcodesmanager.model.DiscountType;
import com.example.discountcodesmanager.model.Product;
import com.example.discountcodesmanager.model.PromoCode;
import com.example.discountcodesmanager.repository.ProductRepository;
import com.example.discountcodesmanager.repository.PromoCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PromoCodeRepository promoCodeRepository;
    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    public void givenNonexistentProduct_whenCalculateDiscountedPrice_thenThrowResourceNotFoundException() {
        // given
        long productId = 1L;
        String promoCode = "DISCOUNT10";
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> purchaseService.calculateDiscountedPrice(productId, promoCode))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product with id: 1, not found");
    }

    @Test
    public void givenNonexistentPromoCode_whenCalculateDiscountedPrice_thenThrowResourceNotFoundException() {
        // given
        Product product = new Product();
        long productId = 1L;
        product.setId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(promoCodeRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> purchaseService.calculateDiscountedPrice(productId, "NONEXISTENT"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Promo code: NONEXISTENT, not found");
    }

    @Test
    public void givenExpiredPromoCode_whenCalculateDiscountedPrice_thenWarningReturned() {
        // given
        Product product = getProduct();
        PromoCode promoCode = new PromoCode();
        promoCode.setExpirationDate(LocalDateTime.now().minusDays(1));
        promoCode.setDiscountCurrency(Currency.getInstance("USD"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(promoCodeRepository.findByCode("EXPIRED")).thenReturn(Optional.of(promoCode));

        // when
        DiscountResponse response = purchaseService.calculateDiscountedPrice(1L, "EXPIRED");

        // then
        assertThat(response.getMessage()).contains("Warning: Promo code has expired");
    }

    @Test
    public void givenCurrencyMismatch_whenCalculateDiscountedPrice_thenWarningReturned() {
        // given
        Product product = getProduct();
        PromoCode promoCode = new PromoCode();
        promoCode.setDiscountCurrency(Currency.getInstance("EUR"));
        promoCode.setExpirationDate(LocalDateTime.now().plusDays(1));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(promoCodeRepository.findByCode("CURRENCYMISMATCH")).thenReturn(Optional.of(promoCode));

        // when
        DiscountResponse response = purchaseService.calculateDiscountedPrice(1L, "CURRENCYMISMATCH");

        // then
        assertThat(response.getMessage()).contains("Warning: Currency mismatch");
    }

    @Test
    public void givenPromoCodeUsageLimitReached_whenCalculateDiscountedPrice_thenWarningReturned() {
        // given
        Product product = getProduct();
        PromoCode promoCode = new PromoCode();
        promoCode.setMaxUsages(10);
        promoCode.setCurrentUsages(10);
        promoCode.setExpirationDate(LocalDateTime.now().plusDays(1));
        promoCode.setDiscountCurrency(Currency.getInstance("USD"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(promoCodeRepository.findByCode("MAXUSAGE")).thenReturn(Optional.of(promoCode));

        // when
        DiscountResponse response = purchaseService.calculateDiscountedPrice(1L, "MAXUSAGE");

        // then
        assertThat(response.getMessage()).contains("Warning: Promo code usage limit reached");
    }

    @Test
    public void givenValidPromoCode_whenCalculateDiscountedPrice_thenDiscountApplied() {
        // given
        Product product = getProduct();
        PromoCode promoCode = new PromoCode();
        promoCode.setDiscountType(DiscountType.PERCENTAGE);
        promoCode.setDiscountAmount(new BigDecimal("10"));
        promoCode.setDiscountCurrency(Currency.getInstance("USD"));
        promoCode.setExpirationDate(LocalDateTime.now().plusDays(1));
        promoCode.setCurrentUsages(0);
        promoCode.setMaxUsages(5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(promoCodeRepository.findByCode("VALIDCODE")).thenReturn(Optional.of(promoCode));

        // when
        DiscountResponse response = purchaseService.calculateDiscountedPrice(1L, "VALIDCODE");

        // then
        assertThat(response.getPrice()).isEqualByComparingTo("90.00");
        assertThat(response.getMessage()).isEqualTo("Promo code applied successfully");
    }

    private static Product getProduct() {
        Product product = new Product();
        product.setCurrency(Currency.getInstance("USD"));
        product.setRegularPrice(new BigDecimal("100.00"));
        return product;
    }
}
