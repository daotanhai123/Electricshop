package com.store.checkout.service.impl;

import com.store.checkout.dto.DiscountDTO;
import com.store.checkout.entity.Discount;
import com.store.checkout.entity.Product;
import com.store.checkout.mapper.DiscountMapper;
import com.store.checkout.repository.DiscountRepository;
import com.store.checkout.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.store.checkout.utils.StringUtils.BUY_ONE_GET_HALF_OFF;

@Service
@AllArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final ProductRepository productRepository;

    private final Logger log = LoggerFactory.getLogger(DiscountService.class);

    public DiscountDTO addDiscount(DiscountDTO discountDTO) {
        validateDiscount(discountDTO);
        Discount discount = discountMapper.dtoToEntity(discountDTO);
        if (discountDTO.getProductIds() != null && !discountDTO.getProductIds().isEmpty()) {
            Set<Product> products = new HashSet<>(productRepository.findAllById(discountDTO.getProductIds()));
            discount.setProducts(products);
            for (Product product : products) {
                product.getDiscounts().add(discount);
            }
        }
        Discount savedDiscount = discountRepository.save(discount);
        productRepository.saveAll(savedDiscount.getProducts());
        return discountMapper.entityToDto(savedDiscount);
    }

    private void validateDiscount(DiscountDTO discountDTO) {
        if (!StringUtils.hasText(discountDTO.getDiscountType())) {
            throw new IllegalArgumentException("Discount type must be provided.");
        }
        if (!BUY_ONE_GET_HALF_OFF.equals(discountDTO.getDiscountType())) {
            throw new IllegalArgumentException("Discount type not supported.");
        }
    }

    public List<Discount> getDiscountForProduct(Product product) {
        return discountRepository.findByProductsContaining(product);
    }

    @Transactional
    public BigDecimal applyDiscountUsage(Discount discount, BigDecimal discountAmtOnBasket, BigDecimal discountAmount) {
        if (discount.getUsageLimit() != null
                && discount.getStartDate() != null
                && discount.getEndDate() != null
                && discountAmtOnBasket.compareTo(BigDecimal.ZERO) != 0) {
            LocalDate now = LocalDate.now();
            if (discount.getUsageCount() >= discount.getUsageLimit()) {
                log.error("Discount usage limit exceeded, discountId: {}", discount.getId());
                return discountAmount;
            } else if (now.isBefore(discount.getStartDate()) || now.isAfter(discount.getEndDate())) {
                log.error("Discount not available, discountId: {}", discount.getId());
                return discountAmount;
            }
            discount.setUsageCount(discount.getUsageCount() + 1);
            discountRepository.save(discount);
            return discountAmount.add(discountAmtOnBasket);
        }
        return discountAmount;
    }
}
