package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.dto.DiscountResponse;
import com.example.discountcodesmanager.dto.PromoCodeRequest;
import com.example.discountcodesmanager.dto.PromoCodeResponse;
import com.example.discountcodesmanager.exception.BadRequestException;
import com.example.discountcodesmanager.exception.ResourceNotFoundException;
import com.example.discountcodesmanager.mapper.PromoCodeMapper;
import com.example.discountcodesmanager.model.DiscountType;
import com.example.discountcodesmanager.model.Product;
import com.example.discountcodesmanager.model.PromoCode;
import com.example.discountcodesmanager.repository.ProductRepository;
import com.example.discountcodesmanager.repository.PromoCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromoCodeService {
    private final PromoCodeRepository promoCodeRepository;
    private final ProductRepository productRepository;
    private final PromoCodeMapper promoCodeMapper;

    public PromoCodeResponse savePromoCode(PromoCodeRequest promoCode) {
        if (promoCodeRepository.findByCode(promoCode.getCode()).isPresent()) {
            throw new BadRequestException("Promo code: " + promoCode.getCode() + ", already exists");
        }
        return PromoCodeMapper.mapToResponse(promoCodeRepository.save(promoCodeMapper.mapFromRequest(promoCode)));
    }

    public List<PromoCodeResponse> getAllPromoCodes() {
        return promoCodeRepository.findAll()
                .stream()
                .map(PromoCodeMapper::mapToResponse)
                .toList();
    }

    public PromoCodeResponse getPromoCodeDetailsByCode(String code) {
        return promoCodeRepository.findByCode(code)
                .map(PromoCodeMapper::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code: " + code + ", not found"));
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

        if (promoCode.getExpirationDate().isBefore(LocalDateTime.now())) {
            return new DiscountResponse(product.getRegularPrice(), "Warning: Promo code has expired");
        }

        if (!promoCode.getDiscountCurrency().equals(product.getCurrency())) {
            return new DiscountResponse(product.getRegularPrice(), "Warning: Currency mismatch");
        }

        if (promoCode.getCurrentUsages() >= promoCode.getMaxUsages()) {
            return new DiscountResponse(product.getRegularPrice(), "Warning: Promo code usage limit reached");
        }
        BigDecimal discountedPrice = getBigDecimal(promoCode, product);

        return new DiscountResponse(discountedPrice.compareTo(BigDecimal.ZERO) > 0 ? discountedPrice : BigDecimal.ZERO, "Promo code applied successfully");
    }

    private static BigDecimal getBigDecimal(PromoCode promoCode, Product product) {
        BigDecimal discountedPrice;
        if (promoCode.getDiscountType() == DiscountType.PERCENTAGE) {
            BigDecimal discountFactor = promoCode.getDiscountAmount().divide(BigDecimal.valueOf(100), 5, RoundingMode.HALF_UP);
            BigDecimal discount = product.getRegularPrice().multiply(discountFactor);
            discountedPrice = product.getRegularPrice().subtract(discount);
        } else {
            discountedPrice = product.getRegularPrice().subtract(promoCode.getDiscountAmount());
        }
        return discountedPrice.setScale(2, RoundingMode.HALF_UP);
    }

}
