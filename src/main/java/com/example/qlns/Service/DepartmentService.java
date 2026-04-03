package com.example.qlns.Service;

import com.example.qlns.Entity.Department;
import java.util.List;

public interface DepartmentService {
    List<Department> getAll();
    Department getById(Long id);
    Department create(Department dept);
    Department update(Long id, Department req);
    Department setManager(Long deptId, Long empId);
    void delete(Long id);
    int countEmployees(Long deptId);
}
