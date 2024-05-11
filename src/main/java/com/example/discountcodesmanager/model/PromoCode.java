package com.example.discountcodesmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    private String code;
    private LocalDateTime expirationDate;
    private BigDecimal discountAmount;
    private Currency discountCurrency;
    private Integer maxUsages;
    private Integer currentUsages;


    public PromoCode(DiscountType discountType, String code,
                     LocalDateTime expirationDate, BigDecimal discountAmount,
                     Currency discountCurrency, Integer maxUsages, Integer currentUsages) {
        this.discountType = discountType;
        this.code = code;
        this.expirationDate = expirationDate;
        this.discountAmount = discountAmount;
        this.discountCurrency = discountCurrency;
        this.maxUsages = maxUsages;
        this.currentUsages = currentUsages;
    }
}
