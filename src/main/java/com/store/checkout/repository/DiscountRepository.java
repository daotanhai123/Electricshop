package com.store.checkout.repository;

import com.store.checkout.entity.Discount;
import com.store.checkout.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByProductsContaining(Product product);
}
