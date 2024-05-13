package com.example.discountcodesmanager.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromoCodeRequest {
    @NotBlank(message = "Discount type is mandatory")
    @Pattern(regexp = "FIXED|PERCENTAGE", message = "Invalid discount type. Allowed values are 'FIXED' or 'PERCENTAGE'")
    private String discountType;
    @NotBlank(message = "Code is mandatory")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,24}$",
            message = "Promo code must be 3-24 alphanumeric characters without whitespaces")
    private String code;
    @NotNull(message = "Expiration date is mandatory")
    @DateTimeFormat(pattern = "yyyyy-MM-dd HH:mm:ss")
    private LocalDateTime expirationDate;
    @NotNull(message = "Discount amount is mandatory")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal discountAmount;
    @NotBlank(message = "Discount currency is mandatory")
    private String discountCurrency;
    @NotNull(message = "Max usages is mandatory")
    @Min(1)
    private Integer maxUsages;
    @NotNull(message = "Current usages is mandatory")
    @Min(0)
    private Integer currentUsages;
}
