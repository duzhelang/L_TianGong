package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.WorkflowLog;
import com.ecocarbon.mrv.entity.WorkflowTask;
import com.ecocarbon.mrv.repository.WorkflowLogRepository;
import com.ecocarbon.mrv.repository.WorkflowTaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkflowService {
    private final WorkflowTaskRepository workflowTaskRepository;
    private final WorkflowLogRepository workflowLogRepository;

    public Page<WorkflowTask> listTasks(Pageable pageable) {
        return workflowTaskRepository.findAll(pageable);
    }

    public List<WorkflowTask> getTasksByProject(Long projectId) {
        return workflowTaskRepository.findByProjectId(projectId);
    }

    public List<WorkflowTask> getTasksByStatus(String status) {
        return workflowTaskRepository.findByStatus(status);
    }

    public List<WorkflowTask> getTasksByAssignedTo(Long assignedTo) {
        return workflowTaskRepository.findByAssignedTo(assignedTo);
    }

    public WorkflowTask getTaskById(Long id) {
        return workflowTaskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("工作流任务不存在: " + id));
    }

    @Transactional
    public WorkflowTask createTask(WorkflowTask task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        if (task.getStatus() == null) {
            task.setStatus("DRAFT");
        }
        if (task.getPriority() == null) {
            task.setPriority("MEDIUM");
        }
        WorkflowTask savedTask = workflowTaskRepository.save(task);
        createLog(savedTask.getId(), "CREATE", task.getCreatedBy(), "创建任务");
        return savedTask;
    }

    @Transactional
    public WorkflowTask updateTask(Long id, WorkflowTask task) {
        WorkflowTask existing = getTaskById(id);
        if (task.getTaskName() != null) {
            existing.setTaskName(task.getTaskName());
        }
        if (task.getTaskType() != null) {
            existing.setTaskType(task.getTaskType());
        }
        if (task.getProjectId() != null) {
            existing.setProjectId(task.getProjectId());
        }
        if (task.getReportId() != null) {
            existing.setReportId(task.getReportId());
        }
        if (task.getPriority() != null) {
            existing.setPriority(task.getPriority());
        }
        if (task.getAssignedTo() != null) {
            existing.setAssignedTo(task.getAssignedTo());
        }
        if (task.getDueDate() != null) {
            existing.setDueDate(task.getDueDate());
        }
        if (task.getDescription() != null) {
            existing.setDescription(task.getDescription());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        return workflowTaskRepository.save(existing);
    }

    @Transactional
    public void deleteTask(Long id) {
        WorkflowTask task = getTaskById(id);
        workflowTaskRepository.delete(task);
    }

    @Transactional
    public WorkflowTask submitTask(Long id, Long operatorId, String operatorName, String comment) {
        WorkflowTask task = getTaskById(id);
        if (!"DRAFT".equals(task.getStatus())) {
            throw new IllegalArgumentException("只有草稿状态的任务可以提交");
        }
        task.setStatus("SUBMITTED");
        task.setUpdatedAt(LocalDateTime.now());
        WorkflowTask savedTask = workflowTaskRepository.save(task);
        createLog(id, "SUBMIT", operatorId, comment != null ? comment : "提交审核");
        return savedTask;
    }

    @Transactional
    public WorkflowTask approveTask(Long id, Long operatorId, String operatorName, String comment) {
        WorkflowTask task = getTaskById(id);
        if (!"SUBMITTED".equals(task.getStatus()) && !"REVIEWING".equals(task.getStatus())) {
            throw new IllegalArgumentException("只有已提交或审核中的任务可以审批");
        }
        task.setStatus("APPROVED");
        task.setUpdatedAt(LocalDateTime.now());
        WorkflowTask savedTask = workflowTaskRepository.save(task);
        createLog(id, "APPROVE", operatorId, comment != null ? comment : "审核通过");
        return savedTask;
    }

    @Transactional
    public WorkflowTask rejectTask(Long id, Long operatorId, String operatorName, String comment) {
        WorkflowTask task = getTaskById(id);
        if (!"SUBMITTED".equals(task.getStatus()) && !"REVIEWING".equals(task.getStatus())) {
            throw new IllegalArgumentException("只有已提交或审核中的任务可以驳回");
        }
        task.setStatus("REJECTED");
        task.setUpdatedAt(LocalDateTime.now());
        WorkflowTask savedTask = workflowTaskRepository.save(task);
        createLog(id, "REJECT", operatorId, comment != null ? comment : "审核驳回");
        return savedTask;
    }

    @Transactional
    public WorkflowTask completeTask(Long id, Long operatorId, String operatorName, String comment) {
        WorkflowTask task = getTaskById(id);
        if (!"APPROVED".equals(task.getStatus())) {
            throw new IllegalArgumentException("只有已批准的任务可以完成");
        }
        task.setStatus("COMPLETED");
        task.setUpdatedAt(LocalDateTime.now());
        WorkflowTask savedTask = workflowTaskRepository.save(task);
        createLog(id, "COMPLETE", operatorId, comment != null ? comment : "任务完成");
        return savedTask;
    }

    @Transactional
    public WorkflowTask cancelTask(Long id, Long operatorId, String operatorName, String comment) {
        WorkflowTask task = getTaskById(id);
        if ("COMPLETED".equals(task.getStatus())) {
            throw new IllegalArgumentException("已完成的任务不能取消");
        }
        task.setStatus("CANCELLED");
        task.setUpdatedAt(LocalDateTime.now());
        WorkflowTask savedTask = workflowTaskRepository.save(task);
        createLog(id, "CANCEL", operatorId, comment != null ? comment : "任务取消");
        return savedTask;
    }

    public List<WorkflowLog> getTaskLogs(Long taskId) {
        return workflowLogRepository.findByTaskId(taskId);
    }

    @Transactional
    public WorkflowLog createLog(Long taskId, String action, Long operatorId, String comment) {
        WorkflowLog log = new WorkflowLog();
        log.setTaskId(taskId);
        log.setAction(action);
        log.setOperatorId(operatorId);
        log.setComment(comment);
        log.setCreatedAt(LocalDateTime.now());
        return workflowLogRepository.save(log);
    }

    public long countByStatus(String status) {
        return workflowTaskRepository.countByStatus(status);
    }

    public long countByProject(Long projectId) {
        return workflowTaskRepository.countByProjectId(projectId);
    }
}
