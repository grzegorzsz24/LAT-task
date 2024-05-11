package com.example.discountcodesmanager.controller;

import com.example.discountcodesmanager.dto.DiscountResponse;
import com.example.discountcodesmanager.dto.PurchaseResponse;
import com.example.discountcodesmanager.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @GetMapping("/discount-price")
    public ResponseEntity<DiscountResponse> getDiscountedPrice(@RequestParam Long productId, @RequestParam String promoCode) {
        return ResponseEntity.ok(purchaseService.calculateDiscountedPrice(productId, promoCode));
    }

    @PostMapping
    public ResponseEntity<PurchaseResponse> createPurchase(@RequestParam Long productId,
                                                           @RequestParam(required = false) String promoCode) {
        return ResponseEntity.ok(purchaseService.createPurchase(productId, promoCode));
    }
}
