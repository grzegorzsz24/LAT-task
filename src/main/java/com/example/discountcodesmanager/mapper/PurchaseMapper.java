package com.example.discountcodesmanager.mapper;

import com.example.discountcodesmanager.dto.PurchaseResponse;
import com.example.discountcodesmanager.model.Purchase;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class PurchaseMapper {
    public static PurchaseResponse mapToResponse(Purchase purchase) {
        PurchaseResponse purchaseResponse = new PurchaseResponse();
        BeanUtils.copyProperties(purchase, purchaseResponse);
        purchaseResponse.setProduct(purchase.getProduct().getName());
        purchaseResponse.setPromoCode(purchase.getPromoCode().getCode());
        purchaseResponse.setCurrency(purchase.getProduct().getCurrency().toString());
        return purchaseResponse;
    }
}
