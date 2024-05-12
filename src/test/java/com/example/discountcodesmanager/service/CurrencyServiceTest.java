package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CurrencyServiceTest {
    @Test
    public void whenValidCurrencyCode_thenCurrencyIsReturned() {
        // given
        CurrencyService currencyService = new CurrencyService();
        String validCurrencyCode = "USD";

        // when
        Optional<Currency> result = currencyService.getCurrency(validCurrencyCode);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getCurrencyCode()).isEqualTo("USD");
    }

    @Test
    public void whenInvalidCurrencyCode_thenBadRequestExceptionIsThrown() {
        // given
        CurrencyService currencyService = new CurrencyService();
        String invalidCurrencyCode = "XYZ";

        // when
        // then
        assertThatThrownBy(() -> currencyService.getCurrency(invalidCurrencyCode))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Currency: XYZ, does not exist");
    }
}
