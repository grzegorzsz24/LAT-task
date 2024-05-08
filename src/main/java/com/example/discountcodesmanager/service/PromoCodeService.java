package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.exception.BadRequestException;
import com.example.discountcodesmanager.exception.ResourceNotFoundException;
import com.example.discountcodesmanager.model.PromoCode;
import com.example.discountcodesmanager.repository.PromoCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromoCodeService {
    private final PromoCodeRepository promoCodeRepository;

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
}
