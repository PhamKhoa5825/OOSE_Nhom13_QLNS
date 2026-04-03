package com.example.qlns.Service.Impl;

import com.example.qlns.DTO.Request.UpdateTaskRequest;
import com.example.qlns.DTO.Response.TaskDTO;
import com.example.qlns.DTO.Response.TaskStatsDTO;
import com.example.qlns.Entity.Department;
import com.example.qlns.Entity.Employee;
import com.example.qlns.Entity.Task;
import com.example.qlns.Entity.TaskUpdate;
import com.example.qlns.Entity.User;
import com.example.qlns.Enum.Role;
import com.example.qlns.Enum.TaskPriority;
import com.example.qlns.Enum.TaskStatus;
import com.example.qlns.Exception.BadRequestException;
import com.example.qlns.Exception.ForbiddenException;
import com.example.qlns.Exception.ResourceNotFoundException;
import com.example.qlns.Mapper.TaskMapper;
import com.example.qlns.Repository.DepartmentRepository;
import com.example.qlns.Repository.EmployeeRepository;
import com.example.qlns.Repository.TaskRepository;
import com.example.qlns.Repository.TaskUpdateRepository;
import com.example.qlns.Repository.UserRepository;
import com.example.qlns.Service.TaskCrudService;
import com.example.qlns.Service.TaskSchedulingService;
import com.example.qlns.Service.TaskStatsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ISP Refactored: Một class implement 3 interface nhỏ thay vì 1 interface lớn.
 * 
 * Trước: implements TaskService (12 methods trong 1 interface)
 * Sau:   implements TaskCrudService (10 methods)
 *                 + TaskSchedulingService (1 method)
 *                 + TaskStatsService (1 method)
 * 
 * Lợi ích:
 * - TaskController chỉ inject TaskCrudService (không cần biết scheduling hay stats)
 * - Spring Scheduler chỉ inject TaskSchedulingService (không cần biết CRUD)
 * - DashboardService chỉ inject TaskStatsService (không cần biết CRUD hay scheduling)
 */
@Service
public class TaskServiceImpl implements TaskCrudService, TaskSchedulingService, TaskStatsService {

    private final TaskRepository taskRepo;
    private final TaskUpdateRepository taskUpdateRepo;
    private final EmployeeRepository empRepo;
    private final UserRepository userRepo;
    private final DepartmentRepository deptRepo;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepo,
                           TaskUpdateRepository taskUpdateRepo,
                           EmployeeRepository empRepo,
                           UserRepository userRepo,
                           DepartmentRepository deptRepo,
                           TaskMapper taskMapper) {
        this.taskRepo = taskRepo;
        this.taskUpdateRepo = taskUpdateRepo;
        this.empRepo = empRepo;
        this.userRepo = userRepo;
        this.deptRepo = deptRepo;
        this.taskMapper = taskMapper;
    }

    // ════════════════════════════════════════════════════════════
    // TaskCrudService — CRUD & nghiệp vụ chính
    // ════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getAll() {
        return taskRepo.findAll().stream().map(taskMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getMyTasks(Long empId) {
        return taskRepo.findByAssignedToId(empId).stream().map(taskMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getByDepartment(Long deptId) {
        return taskRepo.findByDepartmentId(deptId).stream().map(taskMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDTO getById(Long id) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy task id=" + id));
        return taskMapper.toDTO(task);
    }

    @Override
    @Transactional
    public TaskDTO create(Task task) {
        task.setStatus(TaskStatus.PENDING);
        return taskMapper.toDTO(taskRepo.save(task));
    }

    @Override
    @Transactional
    public TaskDTO updateTask(Long taskId, UpdateTaskRequest req) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ id=" + taskId));

        if (task.getStatus() == TaskStatus.DONE)
            throw new BadRequestException("Task đã hoàn thành, không thể chỉnh sửa");

        if (req.getTitle() != null && !req.getTitle().isBlank())
            task.setTitle(req.getTitle().trim());
        if (req.getDescription() != null)
            task.setDescription(req.getDescription());
        if (req.getPriority() != null)
            task.setPriority(TaskPriority.valueOf(req.getPriority()));
        if (req.getDeadline() != null)
            task.setDeadline(java.time.LocalDate.parse(req.getDeadline()).atStartOfDay());
        if (req.getAssignedToId() != null) {
            Employee newAssignee = empRepo.findById(req.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy nhân viên id=" + req.getAssignedToId()));
            task.setAssignedTo(newAssignee);
        }

        return taskMapper.toDTO(taskRepo.save(task));
    }

    @Override
    @Transactional
    public TaskDTO acceptTask(Long taskId, Long employeeId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy task"));

        if (!task.getAssignedTo().getId().equals(employeeId))
            throw new ForbiddenException("Bạn không được giao task này");

        if (task.getStatus() != TaskStatus.PENDING)
            throw new BadRequestException("Chỉ có thể nhận task đang ở trạng thái Chờ nhận");

        task.setStatus(TaskStatus.ACCEPTED);
        saveHistory(task, TaskStatus.ACCEPTED, "Nhân viên đã nhận việc", employeeId);
        return taskMapper.toDTO(taskRepo.save(task));
    }

    @Override
    @Transactional
    public TaskDTO updateStatus(Long taskId, TaskStatus newStatus, String note, Long updatedById) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy task"));

        TaskStatus current = task.getStatus();

        if (current == TaskStatus.DONE)
            throw new BadRequestException("Task đã hoàn thành, không thể thay đổi trạng thái");

        if (current == TaskStatus.PENDING && newStatus == TaskStatus.DONE)
            throw new BadRequestException("Phải nhận việc trước khi đánh dấu hoàn thành");

        task.setStatus(newStatus);
        if (newStatus == TaskStatus.DONE)
            task.setCompletedAt(LocalDateTime.now());

        saveHistory(task, newStatus, note, updatedById);
        return taskMapper.toDTO(taskRepo.save(task));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy task"));

        if (task.getStatus() == TaskStatus.DONE)
            throw new BadRequestException("Không thể xoá task đã hoàn thành");

        taskRepo.delete(task);
    }

    @Override
    public List<TaskUpdate> getHistory(Long taskId) {
        return taskUpdateRepo.findByTaskIdOrderByUpdatedAtDesc(taskId);
    }

    // ════════════════════════════════════════════════════════════
    // TaskSchedulingService — Tác vụ tự động
    // ════════════════════════════════════════════════════════════

    @Override
    @Scheduled(cron = "0 0 8 * * MON-FRI")
    @Transactional
    public void markOverdueTasks() {
        List<Task> overdueTasks = taskRepo.findOverdueTasksToMark(LocalDateTime.now());
        if (!overdueTasks.isEmpty()) {
            overdueTasks.forEach(t -> {
                t.setStatus(TaskStatus.OVERDUE);
                saveHistory(t, TaskStatus.OVERDUE, "Tự động đánh dấu quá hạn bởi hệ thống", null);
            });
            taskRepo.saveAll(overdueTasks);
        }
    }

    // ════════════════════════════════════════════════════════════
    // TaskStatsService — Thống kê
    // ════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public TaskStatsDTO getTaskStats(Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return new TaskStatsDTO(0, 0, 0, 0, 0);

        if (user.getRole() == Role.ADMIN) {
            return new TaskStatsDTO(
                    taskRepo.count(),
                    taskRepo.countByStatus(TaskStatus.DONE),
                    taskRepo.countByStatus(TaskStatus.ACCEPTED),
                    taskRepo.countByStatus(TaskStatus.PENDING),
                    taskRepo.countByStatus(TaskStatus.OVERDUE)
            );
        } else if (user.getRole() == Role.MANAGER) {
            Long deptId = deptRepo.findByManagerId(user.getEmployeeId())
                    .map(Department::getId)
                    .orElse(null);
            if (deptId != null) {
                return new TaskStatsDTO(
                        taskRepo.countByDepartment(deptId),
                        taskRepo.countByDepartmentAndStatus(deptId, TaskStatus.DONE),
                        taskRepo.countByDepartmentAndStatus(deptId, TaskStatus.ACCEPTED),
                        taskRepo.countByDepartmentAndStatus(deptId, TaskStatus.PENDING),
                        taskRepo.countByDepartmentAndStatus(deptId, TaskStatus.OVERDUE)
                );
            }
        }

        Long empId = user.getEmployeeId();
        if (empId != null) {
            return new TaskStatsDTO(
                    taskRepo.countByAssignedToId(empId),
                    taskRepo.countByAssignedToIdAndStatus(empId, TaskStatus.DONE),
                    taskRepo.countByAssignedToIdAndStatus(empId, TaskStatus.ACCEPTED),
                    taskRepo.countByAssignedToIdAndStatus(empId, TaskStatus.PENDING),
                    taskRepo.countByAssignedToIdAndStatus(empId, TaskStatus.OVERDUE)
            );
        }

        return new TaskStatsDTO(0, 0, 0, 0, 0);
    }

    // ════════════════════════════════════════════════════════════
    // Private helpers
    // ════════════════════════════════════════════════════════════

    private void saveHistory(Task task, TaskStatus status, String note, Long updatedById) {
        TaskUpdate log = new TaskUpdate();
        log.setTask(task);
        log.setStatus(status);
        log.setNote(note);
        if (updatedById != null)
            empRepo.findById(updatedById).ifPresent(log::setUpdatedBy);
        taskUpdateRepo.save(log);
    }
}
