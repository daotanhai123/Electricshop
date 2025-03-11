package com.store.checkout.factory.strategy;

import com.store.checkout.entity.Discount;

import java.math.BigDecimal;

public interface DiscountStrategy {
    /**
     * Calculate the discount amount.
     *
     * @param quantity  the quantity purchased
     * @param unitPrice the unit price of the product
     * @param discount  the Discount entity (holds type and parameters)
     * @return discount amount as BigDecimal
     */
    BigDecimal calculateDiscount(int quantity, BigDecimal unitPrice, Discount discount);
}
