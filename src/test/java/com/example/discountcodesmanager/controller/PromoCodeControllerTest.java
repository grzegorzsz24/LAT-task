package com.example.discountcodesmanager.controller;

import com.example.discountcodesmanager.DiscountCodesManagerApplication;
import com.example.discountcodesmanager.dto.PromoCodeRequest;
import com.example.discountcodesmanager.model.DiscountType;
import com.example.discountcodesmanager.model.PromoCode;
import com.example.discountcodesmanager.repository.PromoCodeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DiscountCodesManagerApplication.class)
@AutoConfigureMockMvc
public class PromoCodeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private PromoCodeRepository promoCodeRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        promoCodeRepository.deleteAll();
    }

    @Test
    public void givenValidRequest_whenCreatePromoCode_thenPromoCodeIsCreated() throws Exception {
        //given
        PromoCodeRequest request = new PromoCodeRequest();
        request.setDiscountType("FIXED");
        request.setCode("ABC123");
        request.setExpirationDate(LocalDateTime.now().plusDays(10));
        request.setDiscountAmount(new BigDecimal("10.00"));
        request.setDiscountCurrency("USD");
        request.setMaxUsages(10);
        request.setCurrentUsages(0);

        String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        mockMvc.perform(post("/promo-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))

                //then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is(request.getCode())))
                .andExpect(jsonPath("$.discountCurrency", is(request.getDiscountCurrency())));
    }

    @Test
    public void givenPromoCodes_whenGetAllPromoCodes_thenReturnListOfPromoCodes() throws Exception {
        //given
        PromoCode promoCode = new PromoCode(
                DiscountType.FIXED,
                "PromoCode",
                LocalDateTime.now().plusDays(1),
                new BigDecimal("20.99"),
                Currency.getInstance("USD"),
                5,
                1
        );

        promoCodeRepository.save(promoCode);

        //when
        mockMvc.perform(get("/promo-codes")
                        .accept(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("PromoCode")));
    }

    @Test
    void givenNoExistingPromoCodes_whenGetAllPromoCodes_thenReturnEmptyList() throws Exception {
        //when
        mockMvc.perform(get("/promo-codes"))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void givenExistingCode_whenGetPromoCodeDetails_thenReturnPromoCode() throws Exception {
        //given
        PromoCode promoCode = new PromoCode(
                DiscountType.FIXED,
                "PromoCode",
                LocalDateTime.now().plusDays(1),
                new BigDecimal("20.99"),
                Currency.getInstance("USD"),
                5,
                1
        );

        promoCodeRepository.save(promoCode);

        //when
        mockMvc.perform(get("/promo-codes/{code}", promoCode.getCode())
                        .accept(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk());
    }

    @Test
    public void givenExistingCode_whenCreatePromoCode_thenReturnError() throws Exception {
        //given
        PromoCode promoCode = new PromoCode(
                DiscountType.FIXED,
                "PromoCode",
                LocalDateTime.now().plusDays(1),
                new BigDecimal("20.99"),
                Currency.getInstance("USD"),
                5,
                1
        );

        promoCodeRepository.save(promoCode);

        PromoCodeRequest request = new PromoCodeRequest(
                DiscountType.FIXED.toString(),
                "PromoCode",
                LocalDateTime.now().plusDays(1),
                new BigDecimal("20.00"),
                Currency.getInstance("USD").toString(),
                4,
                0
        );

        String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        mockMvc.perform(post("/promo-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))

                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenInvalidPromoCodeRequest_whenCreatePromoCode_thenReturnError() throws Exception {
        // given
        PromoCodeRequest request = new PromoCodeRequest(
                DiscountType.FIXED.toString(),
                "PromoCode",
                LocalDateTime.now().plusDays(1),
                null,
                Currency.getInstance("USD").toString(),
                4,
                0
        );

        String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        mockMvc.perform(post("/promo-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))

                //then
                .andExpect(status().isBadRequest());
    }
}
