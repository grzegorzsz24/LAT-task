package com.example.discountcodesmanager.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProductRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;
    private String description;
    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.0")
    private BigDecimal regularPrice;
    @NotBlank(message = "Currency is mandatory")
    private String currency;
}
