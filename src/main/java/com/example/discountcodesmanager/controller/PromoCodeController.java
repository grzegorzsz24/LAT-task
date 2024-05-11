package com.example.discountcodesmanager.controller;

import com.example.discountcodesmanager.dto.PromoCodeRequest;
import com.example.discountcodesmanager.dto.PromoCodeResponse;
import com.example.discountcodesmanager.service.PromoCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promo-codes")
@RequiredArgsConstructor
public class PromoCodeController {
    private final PromoCodeService promoCodeService;

    @PostMapping
    public ResponseEntity<PromoCodeResponse> createPromoCode(@Valid @RequestBody PromoCodeRequest promoCode) {
        return new ResponseEntity<>(promoCodeService.savePromoCode(promoCode), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PromoCodeResponse>> getAllPromoCodes() {
        return ResponseEntity.ok(promoCodeService.getAllPromoCodes());
    }

    @GetMapping("/{code}")
    public ResponseEntity<PromoCodeResponse> getPromoCodeDetails(@PathVariable String code) {
        return ResponseEntity.ok(promoCodeService.getPromoCodeDetailsByCode(code));
    }
}
