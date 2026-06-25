package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.WorkflowLog;
import com.ecocarbon.mrv.entity.WorkflowTask;
import com.ecocarbon.mrv.repository.WorkflowLogRepository;
import com.ecocarbon.mrv.repository.WorkflowTaskRepository;
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
class WorkflowServiceTest {

    @Mock
    private WorkflowTaskRepository workflowTaskRepository;

    @Mock
    private WorkflowLogRepository workflowLogRepository;

    @InjectMocks
    private WorkflowService workflowService;

    private WorkflowTask testTask;
    private WorkflowLog testLog;

    @BeforeEach
    void setUp() {
        testTask = new WorkflowTask();
        testTask.setId(1L);
        testTask.setTaskName("测试任务");
        testTask.setTaskType("REPORT_REVIEW");
        testTask.setProjectId(1L);
        testTask.setStatus("DRAFT");
        testTask.setPriority("MEDIUM");
        testTask.setCreatedBy(1L);
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());

        testLog = new WorkflowLog();
        testLog.setId(1L);
        testLog.setTaskId(1L);
        testLog.setAction("CREATE");
        testLog.setOperatorId(1L);
        testLog.setComment("创建任务");
        testLog.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testListTasks() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<WorkflowTask> page = new PageImpl<>(Arrays.asList(testTask));
        when(workflowTaskRepository.findAll(pageable)).thenReturn(page);

        Page<WorkflowTask> result = workflowService.listTasks(pageable);
        assertEquals(1, result.getContent().size());
        verify(workflowTaskRepository).findAll(pageable);
    }

    @Test
    void testGetTasksByProject() {
        List<WorkflowTask> list = Arrays.asList(testTask);
        when(workflowTaskRepository.findByProjectId(1L)).thenReturn(list);

        List<WorkflowTask> result = workflowService.getTasksByProject(1L);
        assertEquals(1, result.size());
        verify(workflowTaskRepository).findByProjectId(1L);
    }

    @Test
    void testGetTasksByStatus() {
        List<WorkflowTask> list = Arrays.asList(testTask);
        when(workflowTaskRepository.findByStatus("DRAFT")).thenReturn(list);

        List<WorkflowTask> result = workflowService.getTasksByStatus("DRAFT");
        assertEquals(1, result.size());
        verify(workflowTaskRepository).findByStatus("DRAFT");
    }

    @Test
    void testGetTaskById() {
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        WorkflowTask result = workflowService.getTaskById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(workflowTaskRepository).findById(1L);
    }

    @Test
    void testGetTaskByIdNotFound() {
        when(workflowTaskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> workflowService.getTaskById(999L));
        verify(workflowTaskRepository).findById(999L);
    }

    @Test
    void testCreateTask() {
        when(workflowTaskRepository.save(any(WorkflowTask.class))).thenReturn(testTask);
        when(workflowLogRepository.save(any(WorkflowLog.class))).thenReturn(testLog);

        WorkflowTask result = workflowService.createTask(testTask);
        assertNotNull(result);
        verify(workflowTaskRepository).save(any(WorkflowTask.class));
        verify(workflowLogRepository).save(any(WorkflowLog.class));
    }

    @Test
    void testUpdateTask() {
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(workflowTaskRepository.save(any(WorkflowTask.class))).thenReturn(testTask);

        WorkflowTask updateData = new WorkflowTask();
        updateData.setTaskName("更新后的任务");

        WorkflowTask result = workflowService.updateTask(1L, updateData);
        assertNotNull(result);
        verify(workflowTaskRepository).save(any(WorkflowTask.class));
    }

    @Test
    void testDeleteTask() {
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        workflowService.deleteTask(1L);
        verify(workflowTaskRepository).delete(testTask);
    }

    @Test
    void testSubmitTask() {
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(workflowTaskRepository.save(any(WorkflowTask.class))).thenReturn(testTask);
        when(workflowLogRepository.save(any(WorkflowLog.class))).thenReturn(testLog);

        WorkflowTask result = workflowService.submitTask(1L, 1L, "测试用户", "提交审核");
        assertNotNull(result);
        assertEquals("SUBMITTED", result.getStatus());
        verify(workflowTaskRepository).save(any(WorkflowTask.class));
    }

    @Test
    void testSubmitTaskInvalidStatus() {
        testTask.setStatus("SUBMITTED");
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        assertThrows(RuntimeException.class, () -> workflowService.submitTask(1L, 1L, "测试用户", "提交审核"));
    }

    @Test
    void testApproveTask() {
        testTask.setStatus("SUBMITTED");
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(workflowTaskRepository.save(any(WorkflowTask.class))).thenReturn(testTask);
        when(workflowLogRepository.save(any(WorkflowLog.class))).thenReturn(testLog);

        WorkflowTask result = workflowService.approveTask(1L, 1L, "审核用户", "审核通过");
        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());
        verify(workflowTaskRepository).save(any(WorkflowTask.class));
    }

    @Test
    void testRejectTask() {
        testTask.setStatus("SUBMITTED");
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(workflowTaskRepository.save(any(WorkflowTask.class))).thenReturn(testTask);
        when(workflowLogRepository.save(any(WorkflowLog.class))).thenReturn(testLog);

        WorkflowTask result = workflowService.rejectTask(1L, 1L, "审核用户", "审核驳回");
        assertNotNull(result);
        assertEquals("REJECTED", result.getStatus());
        verify(workflowTaskRepository).save(any(WorkflowTask.class));
    }

    @Test
    void testCompleteTask() {
        testTask.setStatus("APPROVED");
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(workflowTaskRepository.save(any(WorkflowTask.class))).thenReturn(testTask);
        when(workflowLogRepository.save(any(WorkflowLog.class))).thenReturn(testLog);

        WorkflowTask result = workflowService.completeTask(1L, 1L, "完成用户", "任务完成");
        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        verify(workflowTaskRepository).save(any(WorkflowTask.class));
    }

    @Test
    void testCancelTask() {
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(workflowTaskRepository.save(any(WorkflowTask.class))).thenReturn(testTask);
        when(workflowLogRepository.save(any(WorkflowLog.class))).thenReturn(testLog);

        WorkflowTask result = workflowService.cancelTask(1L, 1L, "取消用户", "任务取消");
        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());
        verify(workflowTaskRepository).save(any(WorkflowTask.class));
    }

    @Test
    void testCancelCompletedTask() {
        testTask.setStatus("COMPLETED");
        when(workflowTaskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        assertThrows(RuntimeException.class, () -> workflowService.cancelTask(1L, 1L, "取消用户", "任务取消"));
    }

    @Test
    void testGetTaskLogs() {
        List<WorkflowLog> list = Arrays.asList(testLog);
        when(workflowLogRepository.findByTaskId(1L)).thenReturn(list);

        List<WorkflowLog> result = workflowService.getTaskLogs(1L);
        assertEquals(1, result.size());
        verify(workflowLogRepository).findByTaskId(1L);
    }

    @Test
    void testCountByStatus() {
        List<WorkflowTask> list = Arrays.asList(testTask);
        when(workflowTaskRepository.findByStatus("DRAFT")).thenReturn(list);

        long count = workflowService.countByStatus("DRAFT");
        assertEquals(1, count);
        verify(workflowTaskRepository).findByStatus("DRAFT");
    }

    @Test
    void testCountByProject() {
        List<WorkflowTask> list = Arrays.asList(testTask);
        when(workflowTaskRepository.findByProjectId(1L)).thenReturn(list);

        long count = workflowService.countByProject(1L);
        assertEquals(1, count);
        verify(workflowTaskRepository).findByProjectId(1L);
    }
}
