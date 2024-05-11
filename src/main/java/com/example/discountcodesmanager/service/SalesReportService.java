package com.example.discountcodesmanager.service;

import com.example.discountcodesmanager.dto.SalesReport;
import com.example.discountcodesmanager.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesReportService {
    private final PurchaseRepository purchaseRepository;

    public List<SalesReport> generateSalesReport() {
        return purchaseRepository.findSalesReportByCurrency().stream()
                .map(result -> new SalesReport(
                        ((Currency) result[0]).getCurrencyCode(),
                        (BigDecimal) result[1],
                        (BigDecimal) result[2],
                        (Long) result[3]))
                .collect(Collectors.toList());
    }
}
