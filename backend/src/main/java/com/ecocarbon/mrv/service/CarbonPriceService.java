package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.CarbonPrice;
import com.ecocarbon.mrv.repository.CarbonPriceRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarbonPriceService {
    private final CarbonPriceRepository priceRepository;

    public List<CarbonPrice> getByExchange(String exchange) {
        return priceRepository.findByExchange(exchange);
    }

    public List<CarbonPrice> getByExchangeAndDateRange(String exchange, LocalDate start, LocalDate end) {
        return priceRepository.findByExchangeAndPriceDateBetween(exchange, start, end);
    }

    public CarbonPrice getLatestPrice(String exchange, String productCode) {
        return priceRepository.findTopByExchangeAndProductCodeOrderByPriceDateDesc(exchange, productCode);
    }

    public CarbonPrice create(CarbonPrice price) {
        price.setCreatedAt(LocalDateTime.now());
        return priceRepository.save(price);
    }

    public List<CarbonPrice> createBatch(List<CarbonPrice> prices) {
        prices.forEach(p -> p.setCreatedAt(LocalDateTime.now()));
        return priceRepository.saveAll(prices);
    }
}
