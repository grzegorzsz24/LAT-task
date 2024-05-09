package com.example.discountcodesmanager.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PromoCodeResponse {
    private Long id;
    private String code;
    private LocalDateTime expirationDate;
    private BigDecimal discountAmount;
    private String discountCurrency;
    private Integer maxUsages;
    private Integer currentUsages;
}
