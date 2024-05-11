package com.example.discountcodesmanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseResponse {
    private Long id;
    private LocalDateTime purchaseDate;
    private BigDecimal regularPrice;
    private BigDecimal discountApplied;
    private String product;
    private String promoCode;
    private String currency;
    private String message;

    public PurchaseResponse(BigDecimal regularPrice, String currency, String message) {
        this.regularPrice = regularPrice;
        this.currency = currency;
        this.message = message;
    }
}
