package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.exception.BadRequestException;
import com.example.discountcodesmanager.exception.ResourceNotFoundException;
import com.example.discountcodesmanager.model.Product;
import com.example.discountcodesmanager.model.PromoCode;
import com.example.discountcodesmanager.repository.ProductRepository;
import com.example.discountcodesmanager.repository.PromoCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromoCodeService {
    private final PromoCodeRepository promoCodeRepository;
    private final ProductRepository productRepository;

    public PromoCode savePromoCode(PromoCode promoCode) {
        if (promoCodeRepository.findByCode(promoCode.getCode()).isPresent()) {
            throw new BadRequestException("Promo code: " + promoCode.getCode() + ", already exists");
        }

        return promoCodeRepository.save(promoCode);
    }

    public List<PromoCode> getAllPromoCodes() {
        return promoCodeRepository.findAll();
    }

    public PromoCode getPromoCodeDetailsByCode(String code) {
        return promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code: " + code + ", not found"));
    }

    public BigDecimal calculateDiscountedPrice(Long productId, String code) {
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

        if (promoCode.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Promo code has expired");
        }

        if (!promoCode.getDiscountCurrency().equals(product.getCurrency())) {
            throw new BadRequestException("Currency mismatch");
        }

        if (promoCode.getCurrentUsages() >= promoCode.getMaxUsages()) {
            throw new BadRequestException("Promo code usage limit reached");
        }

        BigDecimal discountAmount = promoCode.getDiscountAmount();
        BigDecimal discountedPrice = product.getRegularPrice().subtract(discountAmount);

        return discountedPrice.compareTo(BigDecimal.ZERO) > 0 ? discountedPrice : BigDecimal.ZERO;
    }
}
