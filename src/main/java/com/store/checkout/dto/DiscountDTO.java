package com.store.checkout.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class DiscountDTO {
    private Long id;
    private String discountType;
    private String parameters;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer usageLimit;
    @Nullable
    private Set<Long> productIds;
}
