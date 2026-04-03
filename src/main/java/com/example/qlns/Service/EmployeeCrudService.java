package com.example.qlns.Service;

import com.example.qlns.Entity.Employee;
import com.example.qlns.Enum.Role;

/**
 * ISP: Interface chỉ chứa các thao tác CRUD cơ bản cho Employee.
 * Được sử dụng bởi: EmployeeController (create, update, getById)
 */
public interface EmployeeCrudService {
    Employee getById(Long id);
    Employee create(Employee emp, String email, String password, Role role);
    Employee update(Long id, Employee req);
}
