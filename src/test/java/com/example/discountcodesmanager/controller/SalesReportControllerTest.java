package com.example.discountcodesmanager.controller;

import com.example.discountcodesmanager.dto.SalesReport;
import com.example.discountcodesmanager.service.SalesReportService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class SalesReportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalesReportService salesReportService;

    @Test
    public void givenSalesReportsExist_whenGetSalesReport_thenReturnsListOfSalesReports() throws Exception {
        //given
        List<SalesReport> salesReports = new ArrayList<>();
        salesReports.add(new SalesReport("USD", BigDecimal.valueOf(100), BigDecimal.valueOf(20), 5L));
        salesReports.add(new SalesReport("EUR", BigDecimal.valueOf(150), BigDecimal.valueOf(30), 8L));

        Mockito.when(salesReportService.generateSalesReport()).thenReturn(salesReports);

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/sales-report")
                        .contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].currency").value("USD"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].totalAmount").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].totalDiscount").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].numberOfPurchases").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].currency").value("EUR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].totalAmount").value(150))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].totalDiscount").value(30))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].numberOfPurchases").value(8));
    }

    @Test
    public void givenNoSalesReportsExist_whenGetSalesReport_thenReturnsEmptyList() throws Exception {
        //given
        List<SalesReport> emptyList = new ArrayList<>();
        Mockito.when(salesReportService.generateSalesReport()).thenReturn(emptyList);

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/sales-report")
                        .contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }
}