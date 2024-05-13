package com.example.discountcodesmanager.controller;

import com.example.discountcodesmanager.dto.SalesReport;
import com.example.discountcodesmanager.service.SalesReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sales-report")
@RequiredArgsConstructor
public class SalesReportController {
    private final SalesReportService salesReportService;

    @GetMapping
    public ResponseEntity<List<SalesReport>> getSalesReport() {
        return ResponseEntity.ok(salesReportService.generateSalesReport());
    }
}
