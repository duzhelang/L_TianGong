package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.CarbonAsset;
import com.ecocarbon.mrv.repository.CarbonAssetRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarbonAssetService {
    private final CarbonAssetRepository assetRepository;

    public Page<CarbonAsset> list(Pageable pageable) {
        return assetRepository.findAll(pageable);
    }

    public CarbonAsset getById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("碳资产不存在"));
    }

    public CarbonAsset getByCode(String assetCode) {
        return assetRepository.findByAssetCode(assetCode)
                .orElseThrow(() -> new IllegalArgumentException("碳资产不存在: " + assetCode));
    }

    public List<CarbonAsset> getByType(String assetType) {
        return assetRepository.findByAssetType(assetType);
    }

    public List<CarbonAsset> getByOwner(Long ownerId) {
        return assetRepository.findByOwnerId(ownerId);
    }

    public List<CarbonAsset> getActiveAssets() {
        return assetRepository.findByStatus("ACTIVE");
    }

    @Transactional
    public CarbonAsset create(CarbonAsset asset) {
        if (assetRepository.existsByAssetCode(asset.getAssetCode())) {
            throw new IllegalArgumentException("资产编码已存在: " + asset.getAssetCode());
        }
        asset.setStatus("ACTIVE");
        asset.setCreatedAt(LocalDateTime.now());
        asset.setUpdatedAt(LocalDateTime.now());
        return assetRepository.save(asset);
    }

    @Transactional
    public CarbonAsset update(Long id, CarbonAsset asset) {
        CarbonAsset existing = getById(id);
        existing.setAssetName(asset.getAssetName());
        existing.setQuantity(asset.getQuantity());
        existing.setVintageYear(asset.getVintageYear());
        existing.setUpdatedAt(LocalDateTime.now());
        return assetRepository.save(existing);
    }

    @Transactional
    public CarbonAsset retire(Long id) {
        CarbonAsset asset = getById(id);
        asset.setStatus("RETIRED");
        asset.setUpdatedAt(LocalDateTime.now());
        return assetRepository.save(asset);
    }

    @Transactional
    public CarbonAsset transfer(Long id, Long newOwnerId) {
        CarbonAsset asset = getById(id);
        asset.setOwnerId(newOwnerId);
        asset.setStatus("TRADED");
        asset.setUpdatedAt(LocalDateTime.now());
        return assetRepository.save(asset);
    }

    @Transactional
    public void delete(Long id) {
        assetRepository.deleteById(id);
    }
}
