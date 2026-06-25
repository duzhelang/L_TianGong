package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.WorkflowTask;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowTaskRepository extends JpaRepository<WorkflowTask, Long> {
    List<WorkflowTask> findByProjectId(Long projectId);

    List<WorkflowTask> findByStatus(String status);

    List<WorkflowTask> findByAssignedTo(Long assignedTo);

    List<WorkflowTask> findByTaskType(String taskType);

    List<WorkflowTask> findByProjectIdAndStatus(Long projectId, String status);
    
    long countByStatus(String status);
    
    long countByProjectId(Long projectId);
}
