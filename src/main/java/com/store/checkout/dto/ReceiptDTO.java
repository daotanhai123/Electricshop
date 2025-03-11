package com.store.checkout.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDTO {
    private List<ReceiptItemDTO> receiptItemDTOS;
    private BigDecimal totalPrice;
}
