package com.example.qlns.Controller;

import com.example.qlns.DTO.Request.CreateTaskRequest;
import com.example.qlns.DTO.Request.UpdateTaskRequest;
import com.example.qlns.DTO.Request.UpdateTaskStatusRequest;
import com.example.qlns.DTO.Response.TaskDTO;
import com.example.qlns.Entity.Task;
import com.example.qlns.Entity.TaskUpdate;
import com.example.qlns.Enum.TaskPriority;
import com.example.qlns.Enum.TaskStatus;
import com.example.qlns.Exception.ResourceNotFoundException;
import com.example.qlns.Security.SecurityService;
import com.example.qlns.Service.EmployeeCrudService;
import com.example.qlns.Service.TaskCrudService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ISP Refactored Controller.
 * 
 * TRƯỚC:
 *   private final TaskService taskService;  // 12 methods, nhưng Controller không dùng markOverdueTasks() hay getTaskStats()
 * 
 * SAU:
 *   private final TaskCrudService taskService;  // chỉ 10 methods CRUD — đúng cái Controller cần
 *   // TaskSchedulingService → Spring Scheduler tự inject
 *   // TaskStatsService → DashboardService inject
 * 
 * → Controller không bị ép phụ thuộc vào scheduling hay stats.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskCrudService taskService;
    private final EmployeeCrudService empService;
    private final SecurityService securityService;

    TaskController(TaskCrudService taskService, EmployeeCrudService empService, SecurityService securityService) {
        this.taskService = taskService;
        this.empService = empService;
        this.securityService = securityService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAll() {
        return ResponseEntity.ok(taskService.getAll());
    }

    @GetMapping("/my/{empId}")
    public ResponseEntity<List<TaskDTO>> getMyTasks(@PathVariable("empId") Long empId) {
        return ResponseEntity.ok(taskService.getMyTasks(empId));
    }

    @GetMapping("/department/{deptId}")
    public ResponseEntity<List<TaskDTO>> getByDepartment(@PathVariable("deptId") Long deptId) {
        securityService.validateManagerDepartment(deptId);
        return ResponseEntity.ok(taskService.getByDepartment(deptId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TaskDTO> create(@RequestBody CreateTaskRequest req) {
        Task task = new Task();
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setPriority(TaskPriority.valueOf(req.getPriority()));
        if (req.getDeadline() != null)
            task.setDeadline(LocalDate.parse(req.getDeadline()).atStartOfDay());
        task.setAttachmentUrl(req.getAttachmentUrl());
        task.setAssignedTo(empService.getById(req.getAssignedToId()));
        task.setAssignedBy(empService.getById(req.getAssignedById()));
        return ResponseEntity.ok(taskService.create(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> update(@PathVariable("id") Long id,
                                          @RequestBody UpdateTaskRequest req) {
        return ResponseEntity.ok(taskService.updateTask(id, req));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<TaskDTO> acceptTask(@PathVariable("id") Long id,
                                              @RequestBody Map<String, Long> body) {
        Long employeeId = body.get("employeeId");
        if (employeeId == null)
            throw new ResourceNotFoundException("Thiếu employeeId trong body");
        return ResponseEntity.ok(taskService.acceptTask(id, employeeId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateStatus(@PathVariable("id") Long id,
                                                @RequestBody UpdateTaskStatusRequest req,
                                                @RequestParam("updatedById") Long updatedById) {
        return ResponseEntity.ok(taskService.updateStatus(
                id, TaskStatus.valueOf(req.getStatus()), req.getNote(), updatedById));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TaskUpdate>> getHistory(@PathVariable("id") Long id) {
        return ResponseEntity.ok(taskService.getHistory(id));
    }
}
