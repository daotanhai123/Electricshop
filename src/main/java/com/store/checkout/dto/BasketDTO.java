package com.store.checkout.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class BasketDTO {
    private Long customerId;
    private List<BasketItemDTO> basketItems;
    private String status;
}
