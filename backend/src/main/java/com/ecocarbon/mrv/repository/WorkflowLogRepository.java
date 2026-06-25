package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.WorkflowLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowLogRepository extends JpaRepository<WorkflowLog, Long> {
    List<WorkflowLog> findByTaskId(Long taskId);

    List<WorkflowLog> findByOperatorId(Long operatorId);
}
