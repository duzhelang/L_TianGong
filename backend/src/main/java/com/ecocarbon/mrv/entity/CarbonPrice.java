package com.ecocarbon.mrv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "carbon_price")
public class CarbonPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String exchange;

    @Column(nullable = false, length = 32)
    private String productCode;

    @Column(length = 64)
    private String productName;

    @Column(nullable = false)
    private Double price;

    private Double openPrice;

    private Double closePrice;

    private Double highPrice;

    private Double lowPrice;

    private Long volume;

    private Double turnover;

    @Column(nullable = false)
    private LocalDate priceDate;

    private LocalTime priceTime;

    private LocalDateTime createdAt;
}
