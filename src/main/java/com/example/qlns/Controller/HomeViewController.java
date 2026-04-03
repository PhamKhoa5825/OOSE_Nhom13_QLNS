package com.example.qlns.Controller;

import com.example.qlns.DTO.Response.EmployeeDetailDTO;
import com.example.qlns.Entity.Employee;
import com.example.qlns.Entity.User;
import com.example.qlns.Security.SecurityService;
import com.example.qlns.Service.EmployeeCrudService;
import com.example.qlns.Service.EmployeeQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeViewController {

    private final EmployeeCrudService employeeCrudService;
    private final EmployeeQueryService employeeQueryService;
    private final SecurityService securityService;
    private final com.example.qlns.Service.DashboardService dashboardService;

    public HomeViewController(EmployeeCrudService employeeCrudService,
                              EmployeeQueryService employeeQueryService,
                            SecurityService securityService,
                            com.example.qlns.Service.DashboardService dashboardService) {
        this.employeeCrudService = employeeCrudService;
        this.employeeQueryService = employeeQueryService;
        this.securityService = securityService;
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("activePage", "dashboard");
        
        Long userId = securityService.getCurrentUserId();
        if (userId != null) {
            model.addAttribute("dashboard", dashboardService.getDashboardData(userId));
        }
        
        return "dashboard";
    }

    @GetMapping("/profile/{id}")
    public String profile(@PathVariable("id") Long id, Model model) {
        model.addAttribute("activePage", "profile");
        
        Employee emp = employeeCrudService.getById(id);
        if (emp == null) return "redirect:/employees";
        
        EmployeeDetailDTO detail = employeeQueryService.getEmployeeDetail(id);
        
        boolean isAdmin = securityService.isAdmin();
        boolean isOwner = false;
        
        User targetUser = securityService.getUserByEmail(emp.getEmail());
        if (targetUser != null) {
            isOwner = securityService.getCurrentUserId().equals(targetUser.getId());
        }
        
        model.addAttribute("emp", detail);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isOwner", isOwner);
        
        return "profile";
    }
}
