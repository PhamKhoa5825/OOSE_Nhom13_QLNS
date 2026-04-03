package com.example.qlns.Controller;

import com.example.qlns.DTO.Response.EmployeeDTO;
import com.example.qlns.Security.SecurityService;
import com.example.qlns.Service.DepartmentService;
import com.example.qlns.Service.EmployeeQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmployeeViewController {

    private final EmployeeQueryService employeeService;
    private final DepartmentService departmentService;
    private final SecurityService securityService;

    public EmployeeViewController(EmployeeQueryService employeeService,
                                  DepartmentService departmentService,
                                  SecurityService securityService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.securityService = securityService;
    }

    @GetMapping("/employees")
    public String employees(Model model, 
                             @RequestParam(value = "kw", required = false) String kw,
                             @RequestParam(value = "deptId", required = false) Long deptId,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "6") int size) {
        model.addAttribute("activePage", "employees");
        
        boolean isAdmin = securityService.isAdmin();
        Long userDeptId = securityService.getManagerDepartmentId();
        
        if (!isAdmin) {
            deptId = userDeptId;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<EmployeeDTO> employeePage;
        
        if (kw != null && !kw.isEmpty()) {
            employeePage = employeeService.search(kw, deptId, pageable);
        } else if (deptId != null) {
            employeePage = employeeService.getByDepartment(deptId, pageable);
        } else {
            employeePage = employeeService.getAll(pageable);
        }

        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalEmployees", employeePage.getTotalElements());
        model.addAttribute("totalElements", employeePage.getTotalElements());
        model.addAttribute("pageSize", size);
        
        model.addAttribute("departments", departmentService.getAll());
        model.addAttribute("totalDepartments", departmentService.getAll().size());
        model.addAttribute("kw", kw);
        model.addAttribute("selectedDeptId", deptId);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("userRole", securityService.getCurrentUserRole().name());
        
        return "employees";
    }
}
