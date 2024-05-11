package com.example.discountcodesmanager.repository;

import com.example.discountcodesmanager.model.DiscountType;
import com.example.discountcodesmanager.model.PromoCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PromoCodeRepositoryTest {
    @Autowired
    private PromoCodeRepository promoCodeRepository;

    @AfterEach
    void tearDown() {
        promoCodeRepository.deleteAll();
    }

    @Test
    void givenValidPromoCode_whenFindByCode_thenReturnPromoCode() {
        // given
        String code = "123aaa";
        PromoCode promoCode = new PromoCode(
                DiscountType.FIXED,
                code,
                LocalDateTime.now(),
                BigDecimal.valueOf(20),
                Currency.getInstance("USD"),
                5,
                1
        );

        PromoCode expected = promoCodeRepository.save(promoCode);

        // when
        Optional<PromoCode> actual = promoCodeRepository.findByCode(code);

        // then
        assertThat(actual)
                .isNotEmpty()
                .contains(expected);
    }

    @Test
    void givenInvalidPromoCode_whenFindByCode_thenReturnEmptyOptional() {
        // given
        String code = "123aaa";

        // when
        Optional<PromoCode> actual = promoCodeRepository.findByCode(code);

        // then
        assertThat(actual).isEmpty();
    }
}