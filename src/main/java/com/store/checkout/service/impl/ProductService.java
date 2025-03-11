package com.store.checkout.service.impl;

import com.store.checkout.dto.ProductDTO;
import com.store.checkout.entity.Product;
import com.store.checkout.mapper.ProductMapper;
import com.store.checkout.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.store.checkout.utils.StringUtils.ADD_PRODUCT;
import static com.store.checkout.utils.StringUtils.REMOVE_PRODUCT;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public Product createProduct(ProductDTO dto) {
        Product product = productMapper.dtoToEntity(dto);
        validateProduct(product);
        return productRepository.save(product);
    }

    private void validateProduct(Product product) {
        if (!StringUtils.hasText(product.getName())) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be positive.");
        }
        if (product.getQuantity() < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative.");
        }
    }

    public Product getProduct(Long productId) {
        return productRepository
                .findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + productId));
    }

    @Transactional
    public void removeProduct(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (!product.isPresent()) {
            throw new NoSuchElementException("Product not found with id: " + productId);
        }
        productRepository.delete(product.get());
    }

    @Transactional
    public void updateProductQuantity(Long productId, int quantityToAction, String action) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + productId));

        if (ADD_PRODUCT.equals(action)) {
            product.setQuantity(product.getQuantity() + quantityToAction);
        } else if (REMOVE_PRODUCT.equals(action)) {
            if (product.getQuantity() < quantityToAction) {
                throw new IllegalArgumentException("Product stock cannot be negative.");
            }
            product.setQuantity(product.getQuantity() - quantityToAction);
        }
        productRepository.save(product);
    }
}
