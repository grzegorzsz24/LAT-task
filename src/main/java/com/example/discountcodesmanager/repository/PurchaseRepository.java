package com.example.discountcodesmanager.repository;

import com.example.discountcodesmanager.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    @Query("SELECT p.product.currency as currency, SUM(p.regularPrice) as totalAmount, SUM(p.discountApplied) as totalDiscount, COUNT(p) as numberOfPurchases " +
            "FROM Purchase p JOIN p.product GROUP BY p.product.currency")
    List<Object[]> findSalesReportByCurrency();
}
