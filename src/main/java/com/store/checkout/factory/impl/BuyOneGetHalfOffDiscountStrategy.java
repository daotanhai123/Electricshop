package com.store.checkout.factory.impl;

import com.store.checkout.entity.Discount;
import com.store.checkout.factory.strategy.DiscountStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BuyOneGetHalfOffDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculateDiscount(int quantity,
                                        BigDecimal unitPrice,
                                        Discount discount) {
        int numberOfItems = 2; // enhance later
        double percentageOff = 0.5; // enhance later
        int eligibleCount = quantity / numberOfItems;
        return unitPrice
                .multiply(BigDecimal.valueOf(percentageOff))
                .multiply(BigDecimal.valueOf(eligibleCount));
    }
}
