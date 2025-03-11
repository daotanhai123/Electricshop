package com.store.checkout.repository;

import com.store.checkout.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
    // Find basket by unique customerId.
    Optional<Basket> findByCustomerId(Long customerId);
}
