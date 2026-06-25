package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.client.CarbonExchangeApiClient;
import com.ecocarbon.mrv.entity.CarbonPrice;
import com.ecocarbon.mrv.repository.CarbonPriceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarbonPriceSyncService {
    private final CarbonExchangeApiClient exchangeApiClient;
    private final CarbonPriceRepository carbonPriceRepository;

    private static final String[][] PRODUCTS = {
        {"全国碳市场", "CEA", "碳排放配额"},
        {"全国碳市场", "CCER", "核证自愿减排量"},
        {"广州碳排放权交易所", "GDEA", "广东碳配额"},
        {"上海环境能源交易所", "SHEA", "上海碳配额"},
        {"湖北碳排放权交易中心", "HBEA", "湖北碳配额"}
    };

    public List<CarbonPrice> syncLatestPrices() {
        List<CarbonPrice> results = new ArrayList<>();
        for (String[] product : PRODUCTS) {
            JsonNode data = exchangeApiClient.getLatestPrice(product[0], product[1]);
            if (data != null && data.has("price")) {
                CarbonPrice price = parsePrice(data, product);
                if (price != null) {
                    carbonPriceRepository.save(price);
                    results.add(price);
                }
            }
        }
        return results;
    }

    public List<CarbonPrice> syncDailyPrices(String exchange, String productCode,
                                              String startDate, String endDate) {
        JsonNode data = exchangeApiClient.getDailyPrices(exchange, productCode, startDate, endDate);
        List<CarbonPrice> results = new ArrayList<>();
        if (data != null && data.isArray()) {
            for (JsonNode item : data) {
                CarbonPrice price = parseDailyPrice(item, exchange, productCode);
                if (price != null) {
                    carbonPriceRepository.save(price);
                    results.add(price);
                }
            }
        }
        return results;
    }

    public List<CarbonPrice> getLatestFromDb() {
        return carbonPriceRepository.findTop30ByOrderByPriceDateDescPriceTimeDesc();
    }

    public List<CarbonPrice> getByExchangeAndDateRange(String exchange, LocalDate startDate, LocalDate endDate) {
        return carbonPriceRepository.findByExchangeAndPriceDateBetween(exchange, startDate, endDate);
    }

    private CarbonPrice parsePrice(JsonNode data, String[] productInfo) {
        try {
            CarbonPrice price = new CarbonPrice();
            price.setExchange(productInfo[0]);
            price.setProductCode(productInfo[1]);
            price.setProductName(productInfo[2]);
            price.setPrice(data.get("price").asDouble());
            if (data.has("open")) price.setOpenPrice(data.get("open").asDouble());
            if (data.has("close")) price.setClosePrice(data.get("close").asDouble());
            if (data.has("high")) price.setHighPrice(data.get("high").asDouble());
            if (data.has("low")) price.setLowPrice(data.get("low").asDouble());
            if (data.has("volume")) price.setVolume(data.get("volume").asLong());
            if (data.has("turnover")) price.setTurnover(data.get("turnover").asDouble());
            price.setPriceDate(LocalDate.now());
            price.setPriceTime(LocalTime.now());
            return price;
        } catch (Exception e) {
            log.warn("解析碳价数据失败: {}", e.getMessage());
            return null;
        }
    }

    private CarbonPrice parseDailyPrice(JsonNode item, String exchange, String productCode) {
        try {
            CarbonPrice price = new CarbonPrice();
            price.setExchange(exchange);
            price.setProductCode(productCode);
            price.setPrice(item.get("close").asDouble());
            price.setOpenPrice(item.get("open").asDouble());
            price.setClosePrice(item.get("close").asDouble());
            price.setHighPrice(item.get("high").asDouble());
            price.setLowPrice(item.get("low").asDouble());
            if (item.has("volume")) price.setVolume(item.get("volume").asLong());
            if (item.has("turnover")) price.setTurnover(item.get("turnover").asDouble());
            price.setPriceDate(LocalDate.parse(item.get("date").asText()));
            return price;
        } catch (Exception e) {
            log.warn("解析日碳价数据失败: {}", e.getMessage());
            return null;
        }
    }
}
