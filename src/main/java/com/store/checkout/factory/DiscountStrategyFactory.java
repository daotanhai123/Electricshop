package com.store.checkout.factory;

import com.store.checkout.factory.impl.BuyOneGetHalfOffDiscountStrategy;
import com.store.checkout.factory.strategy.DiscountStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.store.checkout.utils.StringUtils.BUY_ONE_GET_HALF_OFF;

@Component
public class DiscountStrategyFactory {
    private final Map<String, DiscountStrategy> strategyMap = new HashMap<>();

    @Autowired
    public DiscountStrategyFactory(List<DiscountStrategy> strategies) {
        for (DiscountStrategy strategy : strategies) {
            if (strategy instanceof BuyOneGetHalfOffDiscountStrategy) {
                strategyMap.put(BUY_ONE_GET_HALF_OFF, strategy);
            }
        }
    }

    public DiscountStrategy getStrategy(String discountType) {
        return strategyMap.get(discountType);
    }
}
