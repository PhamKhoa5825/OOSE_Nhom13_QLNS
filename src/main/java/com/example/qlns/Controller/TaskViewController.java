package com.example.qlns.Controller;

import com.example.qlns.DTO.Response.TaskDTO;
import com.example.qlns.DTO.Response.TaskStatsDTO;
import com.example.qlns.Entity.User;
import com.example.qlns.Enum.Role;
import com.example.qlns.Repository.UserRepository;
import com.example.qlns.Security.SecurityService;
import com.example.qlns.Service.EmployeeQueryService;
import com.example.qlns.Service.TaskCrudService;
import com.example.qlns.Service.TaskStatsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TaskViewController {

    private final TaskCrudService taskCrudService;
    private final TaskStatsService taskStatsService;
    private final SecurityService securityService;
    private final EmployeeQueryService empService;
    private final UserRepository userRepo;

    public TaskViewController(TaskCrudService taskCrudService, TaskStatsService taskStatsService, SecurityService securityService,
                              EmployeeQueryService empService, UserRepository userRepo) {
        this.taskCrudService = taskCrudService;
        this.taskStatsService = taskStatsService;
        this.securityService = securityService;
        this.empService = empService;
        this.userRepo = userRepo;
    }

    @GetMapping("/tasks")
    public String tasks(Model model) {
        Long userId = securityService.getCurrentUserId();
        User user = userRepo.findById(userId).orElseThrow();
        Role role = user.getRole();
        Long empId = user.getEmployeeId();
        
        TaskStatsDTO stats = taskStatsService.getTaskStats(userId);
        model.addAttribute("stats", stats);
        
        List<TaskDTO> tasks;
        boolean isManagerOrAdmin = (role == Role.ADMIN || role == Role.MANAGER);
        model.addAttribute("isManager", isManagerOrAdmin);
        model.addAttribute("currentEmpId", empId);
        
        if (role == Role.ADMIN) {
            tasks = taskCrudService.getAll();
            model.addAttribute("employees", empService.getAll());
        } else if (role == Role.MANAGER) {
            Long deptId = securityService.getManagerDepartmentId();
            tasks = taskCrudService.getByDepartment(deptId);
            model.addAttribute("employees", empService.getByDepartment(deptId));
        } else {
            tasks = taskCrudService.getMyTasks(empId);
        }
        
        model.addAttribute("tasks", tasks);
        model.addAttribute("activePage", "tasks");
        return "tasks";
    }
}
