package com.example.discountcodesmanager.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime purchaseDate;
    private BigDecimal regularPrice;
    private BigDecimal discountApplied;
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;
}
