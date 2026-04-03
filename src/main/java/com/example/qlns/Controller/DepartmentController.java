package com.example.qlns.Controller;

import com.example.qlns.DTO.Response.*;
import com.example.qlns.Entity.*;
import com.example.qlns.Mapper.DepartmentMapper;
import com.example.qlns.Service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentService deptService;
    private final EmployeeQueryService empService;
    private final DepartmentMapper departmentMapper;

    public DepartmentController(DepartmentService deptService,
                                EmployeeQueryService empService,
                                DepartmentMapper departmentMapper) {
        this.deptService = deptService;
        this.empService = empService;
        this.departmentMapper = departmentMapper;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAll() {
        return ResponseEntity.ok(deptService.getAll().stream()
                .map(d -> departmentMapper.toDTO(d, deptService.countEmployees(d.getId())))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getById(@PathVariable("id") Long id) {
        Department d = deptService.getById(id);
        return ResponseEntity.ok(departmentMapper.toDTO(d, deptService.countEmployees(id)));
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<List<EmployeeDTO>> getEmployees(@PathVariable("id") Long id) {
        return ResponseEntity.ok(empService.getByDepartment(id));
    }

    @PostMapping
    public ResponseEntity<DepartmentDTO> create(@RequestBody Department dept) {
        Department saved = deptService.create(dept);
        return ResponseEntity.ok(departmentMapper.toDTO(saved, 0));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> update(@PathVariable("id") Long id, @RequestBody Department req) {
        Department updated = deptService.update(id, req);
        return ResponseEntity.ok(departmentMapper.toDTO(updated, deptService.countEmployees(id)));
    }

    @PutMapping("/{deptId}/manager/{empId}")
    public ResponseEntity<DepartmentDTO> setManager(@PathVariable("deptId") Long deptId, @PathVariable("empId") Long empId) {
        Department updated = deptService.setManager(deptId, empId);
        return ResponseEntity.ok(departmentMapper.toDTO(updated, deptService.countEmployees(deptId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        deptService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
