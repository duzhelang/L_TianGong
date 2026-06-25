package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.ActivityData;
import com.ecocarbon.mrv.entity.CarbonProject;
import com.ecocarbon.mrv.repository.ActivityDataRepository;
import com.ecocarbon.mrv.repository.CarbonProjectRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityDataServiceTest {

    @Mock
    private ActivityDataRepository activityDataRepository;

    @Mock
    private CarbonProjectRepository carbonProjectRepository;

    @InjectMocks
    private ActivityDataService activityDataService;

    private ActivityData testActivityData;
    private CarbonProject testProject;

    @BeforeEach
    void setUp() {
        testProject = new CarbonProject();
        testProject.setId(1L);
        testProject.setName("测试项目");

        testActivityData = new ActivityData();
        testActivityData.setId(1L);
        testActivityData.setProject(testProject);
        testActivityData.setDataType("能源消耗");
        testActivityData.setActivityCategory("柴油");
        testActivityData.setValue(1000.0);
        testActivityData.setUnit("升");
        testActivityData.setPeriodStart(LocalDate.of(2024, 1, 1));
        testActivityData.setPeriodEnd(LocalDate.of(2024, 12, 31));
        testActivityData.setStatus("ACTIVE");
    }

    @Test
    void testList() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ActivityData> page = new PageImpl<>(Arrays.asList(testActivityData));
        when(activityDataRepository.findAll(pageable)).thenReturn(page);

        Page<ActivityData> result = activityDataService.list(pageable);
        assertEquals(1, result.getContent().size());
        verify(activityDataRepository).findAll(pageable);
    }

    @Test
    void testGetByProjectId() {
        List<ActivityData> list = Arrays.asList(testActivityData);
        when(activityDataRepository.findByProjectId(1L)).thenReturn(list);

        List<ActivityData> result = activityDataService.getByProjectId(1L);
        assertEquals(1, result.size());
        verify(activityDataRepository).findByProjectId(1L);
    }

    @Test
    void testGetByDataType() {
        List<ActivityData> list = Arrays.asList(testActivityData);
        when(activityDataRepository.findByDataType("能源消耗")).thenReturn(list);

        List<ActivityData> result = activityDataService.getByDataType("能源消耗");
        assertEquals(1, result.size());
        verify(activityDataRepository).findByDataType("能源消耗");
    }

    @Test
    void testGetById() {
        when(activityDataRepository.findById(1L)).thenReturn(Optional.of(testActivityData));

        ActivityData result = activityDataService.getById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(activityDataRepository).findById(1L);
    }

    @Test
    void testGetByIdNotFound() {
        when(activityDataRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> activityDataService.getById(999L));
        verify(activityDataRepository).findById(999L);
    }

    @Test
    void testCreate() {
        when(carbonProjectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(activityDataRepository.save(any(ActivityData.class))).thenReturn(testActivityData);

        ActivityData result = activityDataService.create(testActivityData);
        assertNotNull(result);
        verify(activityDataRepository).save(any(ActivityData.class));
    }

    @Test
    void testCreateWithProjectNotFound() {
        when(carbonProjectRepository.findById(999L)).thenReturn(Optional.empty());
        testActivityData.getProject().setId(999L);

        assertThrows(RuntimeException.class, () -> activityDataService.create(testActivityData));
    }

    @Test
    void testUpdate() {
        when(activityDataRepository.findById(1L)).thenReturn(Optional.of(testActivityData));
        when(activityDataRepository.save(any(ActivityData.class))).thenReturn(testActivityData);

        ActivityData updateData = new ActivityData();
        updateData.setValue(2000.0);

        ActivityData result = activityDataService.update(1L, updateData);
        assertNotNull(result);
        verify(activityDataRepository).save(any(ActivityData.class));
    }

    @Test
    void testDelete() {
        when(activityDataRepository.findById(1L)).thenReturn(Optional.of(testActivityData));

        activityDataService.delete(1L);
        verify(activityDataRepository).delete(testActivityData);
    }

    @Test
    void testBatchCreate() {
        List<ActivityData> list = Arrays.asList(testActivityData);
        when(carbonProjectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(activityDataRepository.saveAll(list)).thenReturn(list);

        List<ActivityData> result = activityDataService.batchCreate(list);
        assertEquals(1, result.size());
        verify(activityDataRepository).saveAll(list);
    }

    @Test
    void testCountByProjectId() {
        List<ActivityData> list = Arrays.asList(testActivityData);
        when(activityDataRepository.findByProjectId(1L)).thenReturn(list);

        long count = activityDataService.countByProjectId(1L);
        assertEquals(1, count);
        verify(activityDataRepository).findByProjectId(1L);
    }

    @Test
    void testCountByDataType() {
        List<ActivityData> list = Arrays.asList(testActivityData);
        when(activityDataRepository.findByDataType("能源消耗")).thenReturn(list);

        long count = activityDataService.countByDataType("能源消耗");
        assertEquals(1, count);
        verify(activityDataRepository).findByDataType("能源消耗");
    }
}
