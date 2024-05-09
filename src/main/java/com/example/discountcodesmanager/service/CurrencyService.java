package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.Optional;

@Service
public class CurrencyService {
    public Optional<Currency> getCurrency(String currencyCode) {
        try {
            Currency currency = Currency.getInstance(currencyCode.toUpperCase());
            return Optional.of(currency);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Currency: " + currencyCode + ", does not exist");
        }
    }
}
