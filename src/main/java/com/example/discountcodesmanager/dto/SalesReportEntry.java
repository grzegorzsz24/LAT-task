package com.example.discountcodesmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class SalesReportEntry {
    private String currency;
    private BigDecimal totalAmount;
    private BigDecimal totalDiscount;
    private Integer numberOfPurchases;
}
