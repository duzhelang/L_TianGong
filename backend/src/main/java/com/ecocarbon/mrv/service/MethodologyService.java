package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.MethodologyConfig;
import com.ecocarbon.mrv.repository.MethodologyConfigRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MethodologyService {
    private final MethodologyConfigRepository methodologyConfigRepository;

    public MethodologyConfig create(MethodologyConfig methodologyConfig) {
        methodologyConfig.setCreatedAt(LocalDateTime.now());
        methodologyConfig.setUpdatedAt(LocalDateTime.now());
        methodologyConfig.setStatus("ACTIVE");
        return methodologyConfigRepository.save(methodologyConfig);
    }

    public MethodologyConfig update(Long id, MethodologyConfig methodologyConfig) {
        MethodologyConfig existing = methodologyConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("方法学配置不存在"));
        existing.setMethodologyName(methodologyConfig.getMethodologyName());
        existing.setVersion(methodologyConfig.getVersion());
        existing.setDescription(methodologyConfig.getDescription());
        existing.setApplicableScenes(methodologyConfig.getApplicableScenes());
        existing.setCalculationFormula(methodologyConfig.getCalculationFormula());
        existing.setParametersConfig(methodologyConfig.getParametersConfig());
        existing.setReferenceStandard(methodologyConfig.getReferenceStandard());
        existing.setUpdatedAt(LocalDateTime.now());
        return methodologyConfigRepository.save(existing);
    }

    public void delete(Long id) {
        MethodologyConfig existing = methodologyConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("方法学配置不存在"));
        existing.setStatus("INACTIVE");
        existing.setUpdatedAt(LocalDateTime.now());
        methodologyConfigRepository.save(existing);
    }

    public Optional<MethodologyConfig> findById(Long id) {
        return methodologyConfigRepository.findById(id);
    }

    public Optional<MethodologyConfig> findByMethodologyCode(String methodologyCode) {
        return methodologyConfigRepository.findByMethodologyCode(methodologyCode);
    }

    public List<MethodologyConfig> findAll() {
        return methodologyConfigRepository.findByStatus("ACTIVE");
    }

    public List<MethodologyConfig> findByScene(String scene) {
        return methodologyConfigRepository.findByApplicableScenesContaining(scene);
    }
}