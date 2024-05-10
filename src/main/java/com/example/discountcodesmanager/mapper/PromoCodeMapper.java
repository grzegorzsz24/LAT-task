package com.example.discountcodesmanager.mapper;

import com.example.discountcodesmanager.dto.PromoCodeRequest;
import com.example.discountcodesmanager.dto.PromoCodeResponse;
import com.example.discountcodesmanager.model.DiscountType;
import com.example.discountcodesmanager.model.PromoCode;
import com.example.discountcodesmanager.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromoCodeMapper {
    private final CurrencyService currencyService;

    public PromoCode mapFromRequest(PromoCodeRequest request) {
        PromoCode promoCode = new PromoCode();
        BeanUtils.copyProperties(request, promoCode);
        promoCode.setDiscountCurrency(currencyService.getCurrency(request.getDiscountCurrency()).get());
        promoCode.setDiscountType(DiscountType.valueOf(request.getDiscountType()));
        return promoCode;
    }

    public static PromoCodeResponse mapToResponse(PromoCode product) {
        PromoCodeResponse promoCodeResponse = new PromoCodeResponse();
        BeanUtils.copyProperties(product, promoCodeResponse);
        promoCodeResponse.setDiscountCurrency(product.getDiscountCurrency().toString());
        return promoCodeResponse;
    }
}
