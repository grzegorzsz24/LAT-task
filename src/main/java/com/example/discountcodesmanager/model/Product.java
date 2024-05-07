package com.example.discountcodesmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Entity
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    private String description;
    @NotEmpty(message = "Price is mandatory")
    private BigDecimal price;
    @NotBlank(message = "Currency is mandatory")
    private String currency;
}
