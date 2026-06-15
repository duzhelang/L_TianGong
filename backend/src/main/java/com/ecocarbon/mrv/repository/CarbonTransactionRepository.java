package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.CarbonTransaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarbonTransactionRepository extends JpaRepository<CarbonTransaction, Long> {
    Optional<CarbonTransaction> findByTransactionCode(String transactionCode);

    Page<CarbonTransaction> findByCreatedBy(Long createdBy, Pageable pageable);

    List<CarbonTransaction> findByAssetId(Long assetId);

    List<CarbonTransaction> findByStatus(String status);

    Page<CarbonTransaction> findByOrderType(String orderType, Pageable pageable);
}
