package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.dto.PromoCodeRequest;
import com.example.discountcodesmanager.dto.PromoCodeResponse;
import com.example.discountcodesmanager.exception.BadRequestException;
import com.example.discountcodesmanager.exception.ResourceNotFoundException;
import com.example.discountcodesmanager.mapper.PromoCodeMapper;
import com.example.discountcodesmanager.repository.PromoCodeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromoCodeService {
    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeMapper promoCodeMapper;

    @Transactional
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
}
