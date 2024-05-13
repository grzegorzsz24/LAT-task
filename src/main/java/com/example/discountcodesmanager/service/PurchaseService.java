package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.dto.DiscountResponse;
import com.example.discountcodesmanager.dto.PurchaseResponse;
import com.example.discountcodesmanager.exception.PromoCodeValidationException;
import com.example.discountcodesmanager.exception.ResourceNotFoundException;
import com.example.discountcodesmanager.mapper.PurchaseMapper;
import com.example.discountcodesmanager.model.DiscountType;
import com.example.discountcodesmanager.model.Product;
import com.example.discountcodesmanager.model.PromoCode;
import com.example.discountcodesmanager.model.Purchase;
import com.example.discountcodesmanager.repository.ProductRepository;
import com.example.discountcodesmanager.repository.PromoCodeRepository;
import com.example.discountcodesmanager.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final PromoCodeRepository promoCodeRepository;

    private void validatePromoCodeUsage(PromoCode promoCode, Product product) {
        if (promoCode.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new PromoCodeValidationException("Promo code has expired", product.getRegularPrice(), product.getCurrency().toString());
        }

        if (!promoCode.getDiscountCurrency().equals(product.getCurrency())) {
            throw new PromoCodeValidationException("Currency mismatch", product.getRegularPrice(), product.getCurrency().toString());
        }

        if (promoCode.getCurrentUsages() >= promoCode.getMaxUsages()) {
            throw new PromoCodeValidationException("Promo code usage limit reached", product.getRegularPrice(), product.getCurrency().toString());
        }
    }

    public DiscountResponse calculateDiscountedPrice(Long productId, String code) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new ResourceNotFoundException("Product with id: " + productId + ", not found");
        }

        Optional<PromoCode> promoCodeOpt = promoCodeRepository.findByCode(code);
        if (promoCodeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Promo code: " + code + ", not found");
        }

        PromoCode promoCode = promoCodeOpt.get();
        Product product = productOpt.get();

        try {
            validatePromoCodeUsage(promoCode, product);
        } catch (PromoCodeValidationException e) {
            return new DiscountResponse(e.getRegularPrice(), e.getCurrency(), "Warning: " + e.getMessage());
        }

        BigDecimal discountedPrice = calculateDiscountedPrice(promoCode, product);
        return new DiscountResponse(discountedPrice.compareTo(BigDecimal.ZERO) > 0 ? discountedPrice : BigDecimal.ZERO,
                product.getCurrency().toString(),
                "Promo code applied successfully");
    }

    private BigDecimal calculateDiscountedPrice(PromoCode promoCode, Product product) {
        if (promoCode.getDiscountType() == DiscountType.PERCENTAGE) {
            BigDecimal discountFactor = promoCode.getDiscountAmount().divide(BigDecimal.valueOf(100), 5, RoundingMode.HALF_EVEN);
            BigDecimal discountAmount = product.getRegularPrice().multiply(discountFactor);
            return product.getRegularPrice().subtract(discountAmount.setScale(2, RoundingMode.HALF_EVEN));
        } else {
            return product.getRegularPrice().subtract(promoCode.getDiscountAmount());
        }

    }

    public PurchaseResponse createPurchase(Long productId, String promoCode) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id: " + productId + ", not found"));

        BigDecimal discountApplied = BigDecimal.ZERO;

        if (promoCode != null && !promoCode.isEmpty()) {
            if (promoCodeRepository.findByCode(promoCode).isEmpty()) {
                throw new ResourceNotFoundException("Promo code: " + promoCode + ", not found");
            }

            DiscountResponse discountResponse = calculateDiscountedPrice(productId, promoCode);
            if (discountResponse.getMessage().startsWith("Warning:")) {
                return new PurchaseResponse(product.getRegularPrice(), product.getCurrency().toString(), discountResponse.getMessage());
            }
            discountApplied = product.getRegularPrice().subtract(discountResponse.getPrice());
        }

        Purchase purchase = new Purchase();
        purchase.setPurchaseDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        purchase.setRegularPrice(product.getRegularPrice());
        purchase.setDiscountApplied(discountApplied);
        purchase.setProduct(product);

        if (promoCode != null && !promoCode.isEmpty()) {
            PromoCode promoCodeToSave = promoCodeRepository.findByCode(promoCode).get();
            promoCodeToSave.setCurrentUsages(promoCodeToSave.getCurrentUsages() + 1);
            promoCodeRepository.save(promoCodeToSave);
            purchase.setPromoCode(promoCodeRepository.findByCode(promoCode).get());
        }

        return PurchaseMapper.mapToResponse(purchaseRepository.save(purchase));
    }
}
