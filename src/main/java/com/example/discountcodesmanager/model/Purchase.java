package com.example.discountcodesmanager.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime purchaseDate;
    private int regularPrice;
    private int discountApplied;
    @OneToOne
    private Product product;
    @ManyToOne
    private PromoCode promoCode;
}
