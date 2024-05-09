package com.example.discountcodesmanager.mapper;

import com.example.discountcodesmanager.dto.PromoCodeRequest;
import com.example.discountcodesmanager.dto.PromoCodeResponse;
import com.example.discountcodesmanager.model.PromoCode;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class PromoCodeMapper {
    public PromoCode mapFromRequest(PromoCodeRequest request) {
        PromoCode promoCode = new PromoCode();
        BeanUtils.copyProperties(request, promoCode);
        return promoCode;
    }

    public static PromoCodeResponse mapToResponse(PromoCode product) {
        PromoCodeResponse promoCodeResponse = new PromoCodeResponse();
        BeanUtils.copyProperties(product, promoCodeResponse);
        return promoCodeResponse;
    }
}
