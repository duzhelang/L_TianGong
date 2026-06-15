package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.CarbonAsset;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarbonAssetRepository extends JpaRepository<CarbonAsset, Long> {
    Optional<CarbonAsset> findByAssetCode(String assetCode);

    List<CarbonAsset> findByAssetType(String assetType);

    List<CarbonAsset> findByOwnerId(Long ownerId);

    List<CarbonAsset> findByStatus(String status);

    List<CarbonAsset> findByProjectId(Long projectId);

    boolean existsByAssetCode(String assetCode);
}
