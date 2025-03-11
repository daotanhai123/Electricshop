package com.store.checkout.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "discount")
@Getter
@Setter
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "discount_type", nullable = false)
    private String discountType;

    @Column(name = "parameters")
    private String parameters;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "usage_limit", nullable = false)
    private Integer usageLimit;

    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Version
    private Long version;

    @ManyToMany(mappedBy = "discounts", fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();
}
