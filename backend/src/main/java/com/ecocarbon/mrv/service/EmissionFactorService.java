package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.EmissionFactor;
import com.ecocarbon.mrv.repository.EmissionFactorRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmissionFactorService {
    private final EmissionFactorRepository emissionFactorRepository;

    public EmissionFactor create(EmissionFactor emissionFactor) {
        emissionFactor.setCreatedAt(LocalDateTime.now());
        emissionFactor.setUpdatedAt(LocalDateTime.now());
        emissionFactor.setStatus("ACTIVE");
        return emissionFactorRepository.save(emissionFactor);
    }

    public EmissionFactor update(Long id, EmissionFactor emissionFactor) {
        EmissionFactor existing = emissionFactorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("排放因子不存在"));
        existing.setFactorName(emissionFactor.getFactorName());
        existing.setCategory(emissionFactor.getCategory());
        existing.setSubcategory(emissionFactor.getSubcategory());
        existing.setGasType(emissionFactor.getGasType());
        existing.setFactorValue(emissionFactor.getFactorValue());
        existing.setUnit(emissionFactor.getUnit());
        existing.setSource(emissionFactor.getSource());
        existing.setRegion(emissionFactor.getRegion());
        existing.setYear(emissionFactor.getYear());
        existing.setUpdatedAt(LocalDateTime.now());
        return emissionFactorRepository.save(existing);
    }

    public void delete(Long id) {
        EmissionFactor existing = emissionFactorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("排放因子不存在"));
        existing.setStatus("INACTIVE");
        existing.setUpdatedAt(LocalDateTime.now());
        emissionFactorRepository.save(existing);
    }

    public Optional<EmissionFactor> findById(Long id) {
        return emissionFactorRepository.findById(id);
    }

    public Optional<EmissionFactor> findByFactorCode(String factorCode) {
        return emissionFactorRepository.findByFactorCode(factorCode);
    }

    public List<EmissionFactor> findAll() {
        return emissionFactorRepository.findByStatus("ACTIVE");
    }

    public List<EmissionFactor> findByCategory(String category) {
        return emissionFactorRepository.findByCategoryAndStatus(category, "ACTIVE");
    }

    public List<EmissionFactor> findByGasType(String gasType) {
        return emissionFactorRepository.findByGasType(gasType);
    }

    public List<EmissionFactor> findByCategoryAndSubcategory(String category, String subcategory) {
        return emissionFactorRepository.findByCategoryAndSubcategory(category, subcategory);
    }

    public List<EmissionFactor> findBySource(String source) {
        return emissionFactorRepository.findBySource(source);
    }

    public List<EmissionFactor> findByRegion(String region) {
        return emissionFactorRepository.findByRegion(region);
    }

    public List<EmissionFactor> findByCategoryAndGasType(String category, String gasType) {
        return emissionFactorRepository.findActiveByCategoryAndGasType(category, gasType);
    }

    public List<EmissionFactor> search(String keyword) {
        return emissionFactorRepository.searchByKeyword(keyword);
    }

    public List<String> getAllCategories() {
        return emissionFactorRepository.findAllCategories();
    }

    public List<String> getSubcategories(String category) {
        return emissionFactorRepository.findSubcategoriesByCategory(category);
    }

    public EmissionFactor getByFactorCode(String factorCode) {
        return emissionFactorRepository.findByFactorCode(factorCode)
                .orElseThrow(() -> new RuntimeException("排放因子不存在: " + factorCode));
    }
}