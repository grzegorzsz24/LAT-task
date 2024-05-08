package com.example.discountcodesmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private LocalDateTime expirationDate;
    private BigDecimal discountAmount;
    private String discountCurrency;
    private Integer maxUsages;
    private Integer currentUsages;
}
