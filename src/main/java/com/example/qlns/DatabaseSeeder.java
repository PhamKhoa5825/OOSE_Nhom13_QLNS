package com.example.qlns;

import com.example.qlns.Entity.Department;
import com.example.qlns.Entity.Employee;
import com.example.qlns.Entity.User;
import com.example.qlns.Enum.EmployeeStatus;
import com.example.qlns.Enum.Gender;
import com.example.qlns.Enum.Role;
import com.example.qlns.Enum.UserStatus;
import com.example.qlns.Repository.DepartmentRepository;
import com.example.qlns.Repository.EmployeeRepository;
import com.example.qlns.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import java.time.LocalDate;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Chỉ thêm dữ liệu nếu DB đang trống
        if (departmentRepository.count() == 0) {
            System.out.println("Dang tao du lieu mau cho DB...");

            // 1. Tạo Phòng ban (chưa có Manager)
            Department deptIT = new Department("Phòng IT", "Phòng Công nghệ thông tin");
            Department deptHR = new Department("Phòng Hành chính - Nhân sự", "Phòng HC-NS");
            deptIT = departmentRepository.save(deptIT);
            deptHR = departmentRepository.save(deptHR);

            // 2. Tạo Trưởng phòng IT
            Employee managerIT = new Employee();
            managerIT.setFullName("Trần Văn Manager");
            managerIT.setEmail("manager.it@company.com");
            managerIT.setPhone("0987654321");
            managerIT.setPosition("Trưởng phòng IT");
            managerIT.setDepartment(deptIT);
            managerIT.setJoinDate(LocalDate.of(2021, 1, 1));
            managerIT.setGender(Gender.MALE);
            managerIT.setStatus(EmployeeStatus.ACTIVE);
            managerIT = employeeRepository.save(managerIT);

            // Tạo User cho Trưởng phòng IT
            User userManagerIT = new User();
            userManagerIT.setUsername("manager_it");
            userManagerIT.setEmail("manager.it@company.com");
            userManagerIT.setPasswordHash(passwordEncoder.encode("123456")); // Mật khẩu là 123456
            userManagerIT.setRole(Role.MANAGER);
            userManagerIT.setEmployeeId(managerIT.getId());
            userManagerIT.setStatus(UserStatus.ACTIVE);
            userRepository.save(userManagerIT);

            // Cập nhật Manager cho phòng IT
            deptIT.setManager(managerIT);
            departmentRepository.save(deptIT);

            // 3. Tạo Nhân viên phòng IT
            Employee emp1 = new Employee();
            emp1.setFullName("Nguyễn Văn Code");
            emp1.setEmail("nv.code@company.com");
            emp1.setPhone("0912345678");
            emp1.setPosition("Lập trình viên");
            emp1.setDepartment(deptIT);
            emp1.setJoinDate(LocalDate.of(2023, 5, 15));
            emp1.setGender(Gender.MALE);
            emp1.setStatus(EmployeeStatus.ACTIVE);
            emp1 = employeeRepository.save(emp1);

            User userEmp1 = new User();
            userEmp1.setUsername("nv_code");
            userEmp1.setEmail("nv.code@company.com");
            userEmp1.setPasswordHash(passwordEncoder.encode("123456"));
            userEmp1.setRole(Role.EMPLOYEE);
            userEmp1.setEmployeeId(emp1.getId());
            userEmp1.setStatus(UserStatus.ACTIVE);
            userRepository.save(userEmp1);

            // 4. Tạo Nhân viên khác phòng IT
            Employee emp2 = new Employee();
            emp2.setFullName("Lê Thị Test");
            emp2.setEmail("lt.test@company.com");
            emp2.setPhone("0922334455");
            emp2.setPosition("Tester");
            emp2.setDepartment(deptIT);
            emp2.setJoinDate(LocalDate.of(2024, 1, 10));
            emp2.setGender(Gender.FEMALE);
            emp2.setStatus(EmployeeStatus.ACTIVE);
            emp2 = employeeRepository.save(emp2);

            User userEmp2 = new User();
            userEmp2.setUsername("lt_test");
            userEmp2.setEmail("lt.test@company.com");
            userEmp2.setPasswordHash(passwordEncoder.encode("123456"));
            userEmp2.setRole(Role.EMPLOYEE);
            userEmp2.setEmployeeId(emp2.getId());
            userEmp2.setStatus(UserStatus.ACTIVE);
            userRepository.save(userEmp2);

            // 5. Tạo Admin (Không thuộc phòng ban nào)
            User userAdmin = new User();
            userAdmin.setUsername("admin");
            userAdmin.setEmail("admin@company.com");
            userAdmin.setPasswordHash(passwordEncoder.encode("admin123"));
            userAdmin.setRole(Role.ADMIN);
            userAdmin.setStatus(UserStatus.ACTIVE);
            userRepository.save(userAdmin);

            System.out.println("Tao du lieu mau thanh cong!");
            System.out.println("=========================================");
            System.out.println("Tài khoản Trưởng phòng: manager_it / 123456");
            System.out.println("Tài khoản Nhân viên: nv_code / 123456");
            System.out.println("Tài khoản Nhân viên: lt_test / 123456");
            System.out.println("Tài khoản Admin: admin / admin123");
            System.out.println("=========================================");
        }
    }
}
