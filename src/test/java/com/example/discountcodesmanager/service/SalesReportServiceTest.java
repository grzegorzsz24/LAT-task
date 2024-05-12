package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.dto.SalesReport;
import com.example.discountcodesmanager.repository.PurchaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SalesReportServiceTest {
    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private SalesReportService salesReportService;

    @Test
    public void givenMultiCurrencySalesData_whenGenerateSalesReport_thenReportsAreGroupedByCurrency() {
        // given
        Object[] salesDataUSD = {
                Currency.getInstance("USD"),
                new BigDecimal("20000"),
                new BigDecimal("3000"),
                15L
        };
        Object[] salesDataEUR = {
                Currency.getInstance("EUR"),
                new BigDecimal("15000"),
                new BigDecimal("2500"),
                12L
        };
        List<Object[]> mockResult = Arrays.asList(salesDataUSD, salesDataEUR);
        when(purchaseRepository.findSalesReportByCurrency()).thenReturn(mockResult);

        // when
        List<SalesReport> reports = salesReportService.generateSalesReport();

        // then
        assertThat(reports).hasSize(2);

        assertThat(reports.get(0).getCurrency()).isEqualTo("USD");
        assertThat(reports.get(0).getTotalAmount()).isEqualByComparingTo("20000");
        assertThat(reports.get(0).getTotalDiscount()).isEqualByComparingTo("3000");
        assertThat(reports.get(0).getNumberOfPurchases()).isEqualTo(15);

        assertThat(reports.get(1).getCurrency()).isEqualTo("EUR");
        assertThat(reports.get(1).getTotalAmount()).isEqualByComparingTo("15000");
        assertThat(reports.get(1).getTotalDiscount()).isEqualByComparingTo("2500");
        assertThat(reports.get(1).getNumberOfPurchases()).isEqualTo(12);
    }

    @Test
    public void givenNoSalesData_whenGenerateSalesReport_thenEmptyListReturned() {
        // given
        when(purchaseRepository.findSalesReportByCurrency()).thenReturn(List.of());

        // when
        List<SalesReport> reports = salesReportService.generateSalesReport();

        // then
        assertThat(reports).isEmpty();
    }
}
