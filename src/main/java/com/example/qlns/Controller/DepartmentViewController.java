package com.example.qlns.Controller;

import com.example.qlns.DTO.Response.DepartmentDTO;
import com.example.qlns.Mapper.DepartmentMapper;
import com.example.qlns.Service.DepartmentService;
import com.example.qlns.Security.SecurityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/departments")
public class DepartmentViewController {

    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;
    private final SecurityService securityService;

    public DepartmentViewController(DepartmentService departmentService, DepartmentMapper departmentMapper, SecurityService securityService) {
        this.departmentService = departmentService;
        this.departmentMapper = departmentMapper;
        this.securityService = securityService;
    }

    @GetMapping
    public String departments(Model model) {
        List<DepartmentDTO> departments = departmentService.getAll().stream()
                .map(d -> departmentMapper.toDTO(d, departmentService.countEmployees(d.getId())))
                .collect(Collectors.toList());
        
        model.addAttribute("departments", departments);
        model.addAttribute("activePage", "departments");
        model.addAttribute("isAdmin", securityService.isAdmin());
        return "departments";
    }
}
