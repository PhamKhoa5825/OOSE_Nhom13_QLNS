package com.example.qlns.Controller;

import com.example.qlns.DTO.Response.RequestDTO;
import com.example.qlns.DTO.Response.RequestStatsDTO;
import com.example.qlns.Enum.Role;
import com.example.qlns.Security.SecurityService;
import com.example.qlns.Service.RequestService;
import com.example.qlns.Repository.UserRepository;
import com.example.qlns.Entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RequestViewController {

    private final RequestService requestService;
    private final SecurityService securityService;
    private final UserRepository userRepo;

    public RequestViewController(RequestService requestService, 
                                 SecurityService securityService,
                                 UserRepository userRepo) {
        this.requestService = requestService;
        this.securityService = securityService;
        this.userRepo = userRepo;
    }

    @GetMapping("/approvals")
    public String approvals(Model model) {
        Long userId = securityService.getCurrentUserId();
        User user = userRepo.findById(userId).orElseThrow();
        
        List<RequestDTO> requests;
        if (user.getRole() == Role.ADMIN) {
            requests = requestService.getAllRequestsByStatus("PENDING");
        } else if (user.getRole() == Role.MANAGER) {
            Long deptId = securityService.getManagerDepartmentId();
            requests = requestService.getPendingByDepartment(deptId);
        } else {
            return "redirect:/requests";
        }
        
        RequestStatsDTO stats = requestService.getRequestStats(userId);
        model.addAttribute("requests", requests);
        model.addAttribute("stats", stats);
        model.addAttribute("currentEmpId", user.getEmployeeId());
        model.addAttribute("activePage", "approvals");
        return "approvals";
    }

    @GetMapping("/requests")
    public String myRequests(Model model) {
        Long userId = securityService.getCurrentUserId();
        User user = userRepo.findById(userId).orElseThrow();
        Long empId = user.getEmployeeId();
        
        List<RequestDTO> requests = requestService.getMyRequests(empId);
        RequestStatsDTO stats = requestService.getRequestStats(userId);
        
        model.addAttribute("requests", requests);
        model.addAttribute("stats", stats);
        model.addAttribute("currentEmpId", empId);
        model.addAttribute("activePage", "requests");
        return "requests";
    }
}
