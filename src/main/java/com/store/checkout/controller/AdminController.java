package com.store.checkout.controller;

import com.store.checkout.dto.DiscountDTO;
import com.store.checkout.dto.ProductDTO;
import com.store.checkout.mapper.ProductMapper;
import com.store.checkout.service.impl.DiscountService;
import com.store.checkout.service.impl.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin")
@AllArgsConstructor
public class AdminController {
    private final ProductService productService;
    private final DiscountService discountService;
    private final ProductMapper productMapper;

    @PostMapping("/product")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO savedProduct = productMapper.entityToDto(productService.createProduct(productDTO));
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @PostMapping("/discount")
    public ResponseEntity<DiscountDTO> createDiscount(@RequestBody DiscountDTO discountDTO) {
        DiscountDTO savedDiscountDTO = discountService.addDiscount(discountDTO);
        return new ResponseEntity<>(savedDiscountDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.removeProduct(productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
