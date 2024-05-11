package com.example.discountcodesmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime purchaseDate;
    private BigDecimal regularPrice;
    private BigDecimal discountApplied;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;

    public Purchase(LocalDateTime purchaseDate, BigDecimal regularPrice, BigDecimal discountApplied, Product product, PromoCode promoCode) {
        this.purchaseDate = purchaseDate;
        this.regularPrice = regularPrice;
        this.discountApplied = discountApplied;
        this.product = product;
        this.promoCode = promoCode;
    }

}
