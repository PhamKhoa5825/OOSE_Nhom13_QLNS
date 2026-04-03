package com.example.qlns.Service;

import com.example.qlns.DTO.Response.EmployeeDTO;
import com.example.qlns.DTO.Response.EmployeeDetailDTO;
import com.example.qlns.DTO.Response.EmployeeSummaryDTO;
import com.example.qlns.Entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * ISP: Interface chỉ chứa các thao tác truy vấn/tìm kiếm Employee.
 * Được sử dụng bởi: EmployeeController (getAll, search, getByDepartment),
 *                    DashboardServiceImpl (lấy danh sách nhân viên),
 *                    HomeViewController (getEmployeeDetail)
 */
public interface EmployeeQueryService {
    Page<EmployeeDTO> getAll(Pageable pageable);
    List<EmployeeDTO> getAll();
    Page<EmployeeDTO> getByDepartment(Long deptId, Pageable pageable);
    List<EmployeeDTO> getByDepartment(Long deptId);
    Page<EmployeeDTO> search(String keyword, Long deptId, Pageable pageable);
    List<Employee> search(String keyword, Long deptId);
    String getRoleByEmployeeId(Long empId);
    EmployeeSummaryDTO getEmployeeSummary(Long id);
    EmployeeDetailDTO getEmployeeDetail(Long id);
}
