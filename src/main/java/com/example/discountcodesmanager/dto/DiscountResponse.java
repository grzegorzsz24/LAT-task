package com.example.discountcodesmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class DiscountResponse {
    private BigDecimal price;
    private String message;
}
