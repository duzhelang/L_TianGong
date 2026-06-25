package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.CarbonPrice;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarbonPriceRepository extends JpaRepository<CarbonPrice, Long> {
    List<CarbonPrice> findByExchange(String exchange);

    List<CarbonPrice> findByExchangeAndPriceDateBetween(String exchange, LocalDate start, LocalDate end);

    List<CarbonPrice> findByProductCode(String productCode);

    CarbonPrice findTopByExchangeAndProductCodeOrderByPriceDateDesc(String exchange, String productCode);

    List<CarbonPrice> findTop30ByOrderByPriceDateDescPriceTimeDesc();
}
