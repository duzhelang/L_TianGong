package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.ActivityData;
import com.ecocarbon.mrv.entity.CarbonProject;
import com.ecocarbon.mrv.repository.ActivityDataRepository;
import com.ecocarbon.mrv.repository.CarbonProjectRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityDataService {
    private final ActivityDataRepository activityDataRepository;
    private final CarbonProjectRepository carbonProjectRepository;

    public Page<ActivityData> list(Pageable pageable) {
        return activityDataRepository.findAll(pageable);
    }

    public List<ActivityData> getByProjectId(Long projectId) {
        return activityDataRepository.findByProjectId(projectId);
    }

    public List<ActivityData> getByDataType(String dataType) {
        return activityDataRepository.findByDataType(dataType);
    }

    public List<ActivityData> getByActivityCategory(String activityCategory) {
        return activityDataRepository.findByActivityCategory(activityCategory);
    }

    public List<ActivityData> getByProjectIdAndDataType(Long projectId, String dataType) {
        return activityDataRepository.findByProjectIdAndDataType(projectId, dataType);
    }

    public ActivityData getById(Long id) {
        return activityDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("活动数据不存在: " + id));
    }

    @Transactional
    public ActivityData create(ActivityData activityData) {
        if (activityData.getProject() != null && activityData.getProject().getId() != null) {
            CarbonProject project = carbonProjectRepository.findById(activityData.getProject().getId())
                    .orElseThrow(() -> new IllegalArgumentException("项目不存在: " + activityData.getProject().getId()));
            activityData.setProject(project);
        }
        activityData.setCreatedAt(LocalDateTime.now());
        activityData.setUpdatedAt(LocalDateTime.now());
        if (activityData.getStatus() == null) {
            activityData.setStatus("ACTIVE");
        }
        return activityDataRepository.save(activityData);
    }

    @Transactional
    public ActivityData update(Long id, ActivityData activityData) {
        ActivityData existing = getById(id);
        if (activityData.getProject() != null && activityData.getProject().getId() != null) {
            CarbonProject project = carbonProjectRepository.findById(activityData.getProject().getId())
                    .orElseThrow(() -> new IllegalArgumentException("项目不存在: " + activityData.getProject().getId()));
            existing.setProject(project);
        }
        if (activityData.getDataType() != null) {
            existing.setDataType(activityData.getDataType());
        }
        if (activityData.getActivityCategory() != null) {
            existing.setActivityCategory(activityData.getActivityCategory());
        }
        if (activityData.getActivitySubcategory() != null) {
            existing.setActivitySubcategory(activityData.getActivitySubcategory());
        }
        if (activityData.getValue() != null) {
            existing.setValue(activityData.getValue());
        }
        if (activityData.getUnit() != null) {
            existing.setUnit(activityData.getUnit());
        }
        if (activityData.getPeriodStart() != null) {
            existing.setPeriodStart(activityData.getPeriodStart());
        }
        if (activityData.getPeriodEnd() != null) {
            existing.setPeriodEnd(activityData.getPeriodEnd());
        }
        if (activityData.getSource() != null) {
            existing.setSource(activityData.getSource());
        }
        if (activityData.getDataQuality() != null) {
            existing.setDataQuality(activityData.getDataQuality());
        }
        if (activityData.getStatus() != null) {
            existing.setStatus(activityData.getStatus());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        return activityDataRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        ActivityData activityData = getById(id);
        activityDataRepository.delete(activityData);
    }

    @Transactional
    public List<ActivityData> batchCreate(List<ActivityData> activityDataList) {
        LocalDateTime now = LocalDateTime.now();
        for (ActivityData activityData : activityDataList) {
            if (activityData.getProject() != null && activityData.getProject().getId() != null) {
                CarbonProject project = carbonProjectRepository.findById(activityData.getProject().getId())
                        .orElseThrow(() -> new IllegalArgumentException("项目不存在: " + activityData.getProject().getId()));
                activityData.setProject(project);
            }
            activityData.setCreatedAt(now);
            activityData.setUpdatedAt(now);
            if (activityData.getStatus() == null) {
                activityData.setStatus("ACTIVE");
            }
        }
        return activityDataRepository.saveAll(activityDataList);
    }

    public long countByProjectId(Long projectId) {
        return activityDataRepository.countByProjectId(projectId);
    }

    public long countByDataType(String dataType) {
        return activityDataRepository.countByDataType(dataType);
    }
}
