package com.example.discountcodesmanager.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PromoCodeValidationException extends RuntimeException {
    private final BigDecimal regularPrice;
    private final String currency;

    public PromoCodeValidationException(String message, BigDecimal regularPrice, String currency) {
        super(message);
        this.regularPrice = regularPrice;
        this.currency = currency;
    }
}

