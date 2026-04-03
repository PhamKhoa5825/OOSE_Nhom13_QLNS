package com.example.qlns.Service.Impl;

import com.example.qlns.Entity.Department;
import com.example.qlns.Entity.Employee;
import com.example.qlns.Enum.EmployeeStatus;
import com.example.qlns.Exception.BadRequestException;
import com.example.qlns.Exception.DuplicateException;
import com.example.qlns.Exception.ResourceNotFoundException;
import com.example.qlns.Repository.DepartmentRepository;
import com.example.qlns.Repository.EmployeeRepository;
import com.example.qlns.Service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository deptRepo;
    private final EmployeeRepository empRepo;

    public DepartmentServiceImpl(DepartmentRepository deptRepo, EmployeeRepository empRepo) {
        this.deptRepo = deptRepo;
        this.empRepo = empRepo;
    }

    @Override
    public List<Department> getAll() {
        return deptRepo.findAll();
    }

    @Override
    public Department getById(Long id) {
        return deptRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban id=" + id));
    }

    @Override
    @Transactional
    public Department create(Department dept) {
        if (deptRepo.existsByName(dept.getName()))
            throw new DuplicateException("Tên phòng ban đã tồn tại: " + dept.getName());
        return deptRepo.save(dept);
    }

    @Override
    @Transactional
    public Department update(Long id, Department req) {
        Department dept = getById(id);
        dept.setName(req.getName());
        dept.setDescription(req.getDescription());
        return deptRepo.save(dept);
    }

    @Override
    @Transactional
    public Department setManager(Long deptId, Long empId) {
        Department dept = getById(deptId);
        Employee emp = empRepo.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên id=" + empId));
        dept.setManager(emp);
        return deptRepo.save(dept);
    }

    @Override
    public void delete(Long id) {
        Department dept = getById(id);
        long count = empRepo.countByDepartmentIdAndStatus(id, EmployeeStatus.ACTIVE);
        if (count > 0) throw new BadRequestException("Phòng ban còn " + count + " nhân viên đang làm việc");
        deptRepo.delete(dept);
    }

    @Override
    public int countEmployees(Long deptId) {
        return (int) empRepo.countByDepartmentIdAndStatus(deptId, EmployeeStatus.ACTIVE);
    }
}
