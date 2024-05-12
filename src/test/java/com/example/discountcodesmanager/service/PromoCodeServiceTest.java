package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.dto.PromoCodeRequest;
import com.example.discountcodesmanager.dto.PromoCodeResponse;
import com.example.discountcodesmanager.exception.BadRequestException;
import com.example.discountcodesmanager.exception.ResourceNotFoundException;
import com.example.discountcodesmanager.mapper.PromoCodeMapper;
import com.example.discountcodesmanager.model.PromoCode;
import com.example.discountcodesmanager.repository.PromoCodeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PromoCodeServiceTest {
    @Mock
    private PromoCodeRepository promoCodeRepository;

    @Mock
    private PromoCodeMapper promoCodeMapper;

    @InjectMocks
    private PromoCodeService promoCodeService;
    MockedStatic<PromoCodeMapper> mockedPromoCodeMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockedPromoCodeMapper = Mockito.mockStatic(PromoCodeMapper.class);
    }

    @AfterEach
    public void tearDown() {
        mockedPromoCodeMapper.close();
    }

    @Test
    public void givenNewPromoCode_whenSavePromoCode_thenPromoCodeIsSaved() {
        PromoCodeRequest request = new PromoCodeRequest();
        request.setCode("SAVE10");
        request.setExpirationDate(LocalDateTime.now().plusDays(10));
        request.setDiscountAmount(new BigDecimal("10.00"));
        request.setDiscountCurrency("USD");
        request.setMaxUsages(100);
        request.setCurrentUsages(0);

        PromoCode promoCode = new PromoCode();
        promoCode.setCode("SAVE10");

        PromoCodeResponse expectedResponse = new PromoCodeResponse();
        expectedResponse.setCode("SAVE10");

        when(promoCodeRepository.findByCode("SAVE10")).thenReturn(Optional.empty());
        when(promoCodeMapper.mapFromRequest(request)).thenReturn(promoCode);
        when(promoCodeRepository.save(promoCode)).thenReturn(promoCode);
        when(PromoCodeMapper.mapToResponse(promoCode)).thenReturn(expectedResponse);

        PromoCodeResponse actualResponse = promoCodeService.savePromoCode(request);

        assertThat(actualResponse.getCode()).isEqualTo(expectedResponse.getCode());
        verify(promoCodeRepository).save(promoCode);
    }

    @Test
    public void givenExistingPromoCode_whenSavePromoCode_thenThrowBadRequestException() {
        PromoCodeRequest request = new PromoCodeRequest();
        request.setCode("SAVE10");

        when(promoCodeRepository.findByCode("SAVE10")).thenReturn(Optional.of(new PromoCode()));

        assertThatThrownBy(() -> promoCodeService.savePromoCode(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    public void whenGetAllPromoCodes_thenAllPromoCodesAreReturned() {
        List<PromoCode> promoCodes = Arrays.asList(new PromoCode(), new PromoCode());
        List<PromoCodeResponse> expectedResponses = Arrays.asList(new PromoCodeResponse(), new PromoCodeResponse());

        when(promoCodeRepository.findAll()).thenReturn(promoCodes);
        when(PromoCodeMapper.mapToResponse(any(PromoCode.class))).thenReturn(expectedResponses.get(0), expectedResponses.get(1));

        List<PromoCodeResponse> actualResponses = promoCodeService.getAllPromoCodes();

        assertThat(actualResponses).hasSize(2);
        verify(promoCodeRepository).findAll();
    }

    @Test
    public void givenValidCode_whenGetPromoCodeDetailsByCode_thenPromoCodeDetailsAreReturned() {
        PromoCode promoCode = new PromoCode();
        promoCode.setCode("SAVE10");
        PromoCodeResponse expectedResponse = new PromoCodeResponse();
        expectedResponse.setCode("SAVE10");

        when(promoCodeRepository.findByCode("SAVE10")).thenReturn(Optional.of(promoCode));
        when(PromoCodeMapper.mapToResponse(promoCode)).thenReturn(expectedResponse);

        PromoCodeResponse actualResponse = promoCodeService.getPromoCodeDetailsByCode("SAVE10");

        assertThat(actualResponse.getCode()).isEqualTo("SAVE10");
    }

    @Test
    public void givenInvalidCode_whenGetPromoCodeDetailsByCode_thenThrowResourceNotFoundException() {
        when(promoCodeRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> promoCodeService.getPromoCodeDetailsByCode("INVALID"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }
}
