package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.dto.CreateProjectRequest;
import com.ecocarbon.mrv.entity.CarbonProject;
import com.ecocarbon.mrv.repository.CarbonProjectRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarbonProjectService {
    private final CarbonProjectRepository projectRepository;

    public CarbonProject create(CreateProjectRequest request) {
        CarbonProject project = new CarbonProject();
        project.setName(request.name());
        project.setSceneType(request.sceneType());
        project.setAreaName(request.areaName());
        project.setAreaHa(request.areaHa());
        project.setCreatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    public List<CarbonProject> list() {
        return projectRepository.findAll();
    }
}
