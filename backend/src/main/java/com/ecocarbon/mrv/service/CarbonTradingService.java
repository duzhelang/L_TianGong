package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.CarbonTransaction;
import com.ecocarbon.mrv.repository.CarbonTransactionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarbonTradingService {
    private final CarbonTransactionRepository transactionRepository;

    public Page<CarbonTransaction> list(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    public CarbonTransaction getById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("交易记录不存在"));
    }

    public CarbonTransaction getByCode(String transactionCode) {
        return transactionRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new IllegalArgumentException("交易记录不存在: " + transactionCode));
    }

    public Page<CarbonTransaction> getByUser(Long userId, Pageable pageable) {
        return transactionRepository.findByCreatedBy(userId, pageable);
    }

    public List<CarbonTransaction> getByAsset(Long assetId) {
        return transactionRepository.findByAssetId(assetId);
    }

    @Transactional
    public CarbonTransaction createOrder(CarbonTransaction transaction) {
        transaction.setTransactionCode(generateTransactionCode());
        transaction.setStatus("PENDING");
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public CarbonTransaction submitOrder(Long id) {
        CarbonTransaction transaction = getById(id);
        if (!"PENDING".equals(transaction.getStatus())) {
            throw new IllegalArgumentException("订单状态不正确，无法提交");
        }
        transaction.setStatus("SUBMITTED");
        transaction.setSubmittedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public CarbonTransaction matchOrder(Long id) {
        CarbonTransaction transaction = getById(id);
        if (!"SUBMITTED".equals(transaction.getStatus())) {
            throw new IllegalArgumentException("订单状态不正确，无法撮合");
        }
        transaction.setStatus("MATCHED");
        transaction.setMatchedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public CarbonTransaction settleOrder(Long id) {
        CarbonTransaction transaction = getById(id);
        if (!"MATCHED".equals(transaction.getStatus())) {
            throw new IllegalArgumentException("订单状态不正确，无法结算");
        }
        transaction.setStatus("SETTLED");
        transaction.setSettledAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public CarbonTransaction cancelOrder(Long id) {
        CarbonTransaction transaction = getById(id);
        if ("SETTLED".equals(transaction.getStatus()) || "COMPLETED".equals(transaction.getStatus())) {
            throw new IllegalArgumentException("订单已完成，无法取消");
        }
        transaction.setStatus("CANCELLED");
        transaction.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    private String generateTransactionCode() {
        return "TX" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
