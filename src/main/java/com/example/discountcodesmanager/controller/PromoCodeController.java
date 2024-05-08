package com.example.discountcodesmanager.controller;

import com.example.discountcodesmanager.model.PromoCode;
import com.example.discountcodesmanager.service.PromoCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/promo-codes")
@RequiredArgsConstructor
public class PromoCodeController {
    private final PromoCodeService promoCodeService;

    @PostMapping
    public ResponseEntity<PromoCode> createPromoCode(@Valid @RequestBody PromoCode promoCode) {
        return new ResponseEntity<>(promoCodeService.savePromoCode(promoCode), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PromoCode>> getAllPromoCodes() {
        return ResponseEntity.ok(promoCodeService.getAllPromoCodes());
    }

    @GetMapping("/{code}")
    public ResponseEntity<PromoCode> getPromoCodeDetails(@PathVariable String code) {
        return ResponseEntity.ok(promoCodeService.getPromoCodeDetailsByCode(code));
    }

    @GetMapping("/discount-price")
    public ResponseEntity<?> getDiscountedPrice(@RequestParam Long productId, @RequestParam String promoCode) {
        return ResponseEntity.ok(Collections.singletonMap(
                "discountedPrice",
                promoCodeService.calculateDiscountedPrice(productId, promoCode)));
    }
}
