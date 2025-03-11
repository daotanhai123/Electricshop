package com.store.checkout.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import static com.store.checkout.utils.StringUtils.ACTIVE;

@Entity
@Table(name = "basket")
@Getter
@Setter
@NoArgsConstructor
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long customerId;

    @OneToMany(mappedBy = "basket",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<BasketItem> basketItems = new ArrayList<>();

    @Column(nullable = false)
    private String status = ACTIVE;

    public Basket(Long customerId) {
        this.customerId = customerId;
    }

    public void addBasketItem(BasketItem basketItem) {
        this.basketItems.add(basketItem);
        basketItem.setBasket(this);
    }

    public void removeBasketItem(BasketItem basketItem) {
        basketItems.remove(basketItem);
        basketItem.setBasket(null);
    }

}
