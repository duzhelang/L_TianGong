package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.EmissionFactor;
import com.ecocarbon.mrv.repository.EmissionFactorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("排放因子服务测试")
class EmissionFactorServiceTest {

    @Mock
    private EmissionFactorRepository emissionFactorRepository;

    @InjectMocks
    private EmissionFactorService emissionFactorService;

    private EmissionFactor testFactor;

    @BeforeEach
    void setUp() {
        testFactor = new EmissionFactor();
        testFactor.setId(1L);
        testFactor.setFactorCode("EF-CO2-001");
        testFactor.setFactorName("柴油燃烧排放因子");
        testFactor.setCategory("ENERGY");
        testFactor.setGasType("CO2");
        testFactor.setFactorValue(2.68);
        testFactor.setUnit("kgCO2/L");
        testFactor.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("获取所有排放因子 - 成功")
    void findAll_Success() {
        List<EmissionFactor> factors = Arrays.asList(testFactor);
        when(emissionFactorRepository.findByStatus("ACTIVE")).thenReturn(factors);

        List<EmissionFactor> result = emissionFactorService.findAll();

        assertEquals(1, result.size());
        assertEquals("EF-CO2-001", result.get(0).getFactorCode());
    }

    @Test
    @DisplayName("根据ID获取排放因子 - 成功")
    void findById_Success() {
        when(emissionFactorRepository.findById(1L)).thenReturn(Optional.of(testFactor));

        Optional<EmissionFactor> result = emissionFactorService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("EF-CO2-001", result.get().getFactorCode());
    }

    @Test
    @DisplayName("根据ID获取排放因子 - 不存在")
    void findById_NotFound() {
        when(emissionFactorRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<EmissionFactor> result = emissionFactorService.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("根据编码获取排放因子 - 成功")
    void findByFactorCode_Success() {
        when(emissionFactorRepository.findByFactorCode("EF-CO2-001"))
                .thenReturn(Optional.of(testFactor));

        Optional<EmissionFactor> result = emissionFactorService.findByFactorCode("EF-CO2-001");

        assertTrue(result.isPresent());
        assertEquals(2.68, result.get().getFactorValue());
    }

    @Test
    @DisplayName("根据类别获取排放因子 - 成功")
    void findByCategory_Success() {
        List<EmissionFactor> factors = Arrays.asList(testFactor);
        when(emissionFactorRepository.findByCategoryAndStatus("ENERGY", "ACTIVE"))
                .thenReturn(factors);

        List<EmissionFactor> result = emissionFactorService.findByCategory("ENERGY");

        assertEquals(1, result.size());
        assertEquals("ENERGY", result.get(0).getCategory());
    }

    @Test
    @DisplayName("创建排放因子 - 成功")
    void create_Success() {
        when(emissionFactorRepository.save(any(EmissionFactor.class))).thenReturn(testFactor);

        EmissionFactor result = emissionFactorService.create(testFactor);

        assertNotNull(result);
        assertEquals("EF-CO2-001", result.getFactorCode());
        verify(emissionFactorRepository).save(testFactor);
    }

    @Test
    @DisplayName("更新排放因子 - 成功")
    void update_Success() {
        when(emissionFactorRepository.findById(1L)).thenReturn(Optional.of(testFactor));
        when(emissionFactorRepository.save(any(EmissionFactor.class))).thenReturn(testFactor);

        EmissionFactor updateData = new EmissionFactor();
        updateData.setFactorName("更新后的名称");
        updateData.setFactorValue(3.0);

        EmissionFactor result = emissionFactorService.update(1L, updateData);

        assertNotNull(result);
        verify(emissionFactorRepository).save(any(EmissionFactor.class));
    }

    @Test
    @DisplayName("删除排放因子 - 成功")
    void delete_Success() {
        when(emissionFactorRepository.findById(1L)).thenReturn(Optional.of(testFactor));
        when(emissionFactorRepository.save(any(EmissionFactor.class))).thenReturn(testFactor);

        assertDoesNotThrow(() -> emissionFactorService.delete(1L));

        assertEquals("INACTIVE", testFactor.getStatus());
        verify(emissionFactorRepository).save(testFactor);
    }
}
