package com.store.checkout.controller;

import com.store.checkout.dto.BasketDTO;
import com.store.checkout.dto.BasketItemDTO;
import com.store.checkout.dto.ReceiptDTO;
import com.store.checkout.service.impl.BasketService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/basket")
@AllArgsConstructor
public class CustomerController {
    private final BasketService basketService;

    @PostMapping("/{customerId}/item")
    public ResponseEntity<BasketDTO> addProductToBasket(@RequestBody BasketItemDTO basketItemDTO, @PathVariable Long customerId) {
        BasketDTO basketDTO = basketService.addProductToBasket(basketItemDTO, customerId);
        return new ResponseEntity<>(basketDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}/item")
    public ResponseEntity<BasketDTO> removeProductFromBasket(@RequestBody BasketItemDTO basketItemDTO, @PathVariable Long customerId) {
        BasketDTO basketDTO = basketService.removeProductFromBasket(basketItemDTO, customerId);
        return new ResponseEntity<>(basketDTO, HttpStatus.OK);
    }

    @GetMapping("/{customerId}/receipt")
    public ResponseEntity<ReceiptDTO> calculateReceipt(@PathVariable Long customerId) {
        ReceiptDTO receiptDTO = basketService.calculateReceipt(customerId);
        return new ResponseEntity<>(receiptDTO, HttpStatus.OK);
    }
}
