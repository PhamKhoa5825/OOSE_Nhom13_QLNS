package com.example.qlns.Service;

import com.example.qlns.DTO.Request.UpdateTaskRequest;
import com.example.qlns.DTO.Response.TaskDTO;
import com.example.qlns.Entity.Task;
import com.example.qlns.Entity.TaskUpdate;
import com.example.qlns.Enum.TaskStatus;

import java.util.List;

/**
 * ISP: Interface chứa các thao tác CRUD và nghiệp vụ chính của Task.
 * Được sử dụng bởi: TaskController (create, update, delete, accept, updateStatus)
 */
public interface TaskCrudService {
    List<TaskDTO> getAll();
    List<TaskDTO> getMyTasks(Long empId);
    List<TaskDTO> getByDepartment(Long deptId);
    TaskDTO getById(Long id);
    TaskDTO create(Task task);
    TaskDTO updateTask(Long taskId, UpdateTaskRequest req);
    TaskDTO acceptTask(Long taskId, Long employeeId);
    TaskDTO updateStatus(Long taskId, TaskStatus newStatus, String note, Long updatedById);
    void delete(Long id);
    List<TaskUpdate> getHistory(Long taskId);
}
