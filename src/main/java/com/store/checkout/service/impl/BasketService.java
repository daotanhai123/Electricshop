package com.store.checkout.service.impl;

import com.store.checkout.dto.BasketDTO;
import com.store.checkout.dto.BasketItemDTO;
import com.store.checkout.dto.ReceiptDTO;
import com.store.checkout.dto.ReceiptItemDTO;
import com.store.checkout.entity.Basket;
import com.store.checkout.entity.BasketItem;
import com.store.checkout.entity.Discount;
import com.store.checkout.entity.Product;
import com.store.checkout.factory.DiscountStrategyFactory;
import com.store.checkout.factory.strategy.DiscountStrategy;
import com.store.checkout.mapper.BasketMapper;
import com.store.checkout.repository.BasketRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.store.checkout.utils.StringUtils.CHECK_OUT;
import static com.store.checkout.utils.StringUtils.REMOVE_PRODUCT;

@Service
@AllArgsConstructor
public class BasketService {
    private final BasketRepository basketRepository;
    private final ProductService productService;
    private final DiscountService discountService;
    private final DiscountStrategyFactory discountStrategyFactory;
    private final BasketMapper basketMapper;

    private final Logger logger = LoggerFactory.getLogger(BasketService.class);

    @Transactional
    public Basket getOrCreateBasket(Long customerId) {
        return basketRepository.findByCustomerId(customerId)
                .orElseGet(() -> basketRepository.save(new Basket(customerId)));
    }

    @Transactional
    public BasketDTO addProductToBasket(BasketItemDTO basketItemDTO, Long customerId) {
        int quantityToBeIncreased = basketItemDTO.getQuantity();
        Long productId = basketItemDTO.getProduct().getId();
        if (quantityToBeIncreased <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        Product product = productService.getProduct(productId);
        Basket basket = getOrCreateBasket(customerId);
        Optional<BasketItem> existingBasketItem = basket
                .getBasketItems()
                .stream()
                .filter(basketItem -> basketItem.getProduct().getId().equals(productId))
                .findFirst();
        if (existingBasketItem.isPresent()) {
            BasketItem basketItem = existingBasketItem.get();
            basketItem.setQuantity(basketItem.getQuantity() + quantityToBeIncreased);
        } else {
            BasketItem newBasketItem = new BasketItem(quantityToBeIncreased, product);
            basket.addBasketItem(newBasketItem);
        }
        return basketMapper.entityToDto(basketRepository.save(basket));
    }

    @Transactional
    public BasketDTO removeProductFromBasket(BasketItemDTO basketItemDTO, Long customerId) {
        int quantityToBeDecreased = basketItemDTO.getQuantity();
        Long productId = basketItemDTO.getProduct().getId();
        if (quantityToBeDecreased < 0) {
            quantityToBeDecreased = 0;
        }
        Basket basket = getOrCreateBasket(customerId);
        if (CHECK_OUT.equals(basket.getStatus())) {
            logger.info("Basket {} is checked out", basket.getId());
            return null;
        }
        Optional<BasketItem> existingBasketItem = basket.getBasketItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        if (!existingBasketItem.isPresent()) {
            throw new IllegalArgumentException("Basket item not found");
        }
        int newProductQuantity = existingBasketItem.get().getQuantity() - quantityToBeDecreased;
        if (newProductQuantity > 0) {
            existingBasketItem.get().setQuantity(newProductQuantity);
        } else {
            basket.removeBasketItem(existingBasketItem.get());
        }

        return basketMapper.entityToDto(basketRepository.save(basket));
    }

    @Transactional
    public ReceiptDTO calculateReceipt(Long customerId) {
        Basket basket = getOrCreateBasket(customerId);
        if (CHECK_OUT.equals(basket.getStatus())) {
            logger.info("Basket with id: {} is checked out", basket.getId());
            return null;
        }
        List<ReceiptItemDTO> receiptItemDTOS = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (BasketItem basketItem : basket.getBasketItems()) {
            int quantity = basketItem.getQuantity();
            Product product = basketItem.getProduct();
            BigDecimal unitPrice = product.getPrice();
            BigDecimal basketItemTotalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
            BigDecimal discountAmount = BigDecimal.ZERO;
            List<Discount> discounts = discountService.getDiscountForProduct(product);
            for (Discount discount : discounts) {
                DiscountStrategy strategy = discountStrategyFactory.getStrategy(discount.getDiscountType());
                if (strategy != null) {
                    BigDecimal discountAmtOnBasket = strategy.calculateDiscount(quantity, unitPrice, discount);
                    discountAmount = discountService.applyDiscountUsage(discount, discountAmtOnBasket, discountAmount);
                }
            }
            if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                basketItemTotalPrice = basketItemTotalPrice.subtract(discountAmount);
            }
            total = total.add(basketItemTotalPrice);
            ReceiptItemDTO receiptItemDTO = new ReceiptItemDTO(
                    product.getName(),
                    quantity,
                    unitPrice,
                    discountAmount,
                    basketItemTotalPrice
            );
            receiptItemDTOS.add(receiptItemDTO);
            productService.updateProductQuantity(product.getId(), quantity, REMOVE_PRODUCT);
        }
        basket.setStatus(CHECK_OUT);
        basketRepository.save(basket);
        return new ReceiptDTO(receiptItemDTOS, total);
    }

}
