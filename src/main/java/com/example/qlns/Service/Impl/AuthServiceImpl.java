package com.example.qlns.Service.Impl;

import com.example.qlns.DTO.Request.AuthenticationRequest;
import com.example.qlns.DTO.Request.ChangePasswordRequest;
import com.example.qlns.DTO.Request.UserRegistrationRequest;
import com.example.qlns.DTO.Response.AuthenticationResponse;
import com.example.qlns.Entity.User;
import com.example.qlns.Enum.Role;
import com.example.qlns.Enum.UserStatus;
import com.example.qlns.Exception.DuplicateException;
import com.example.qlns.Exception.ResourceNotFoundException;
import com.example.qlns.Repository.UserRepository;
import com.example.qlns.Repository.EmployeeRepository;
import com.example.qlns.Security.JwtService;
import com.example.qlns.Security.UserDetailsImpl;
import com.example.qlns.Service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmployeeRepository empRepo;

    public AuthServiceImpl(UserRepository userRepository, 
                           PasswordEncoder passwordEncoder, 
                           AuthenticationManager authenticationManager, 
                           JwtService jwtService, 
                           EmployeeRepository empRepo) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.empRepo = empRepo;
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Long employeeId = user.getEmployeeId();
            Long departmentId = (employeeId != null) ? empRepo.findDepartmentIdByEmployeeId(employeeId) : null;

            return new AuthenticationResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    departmentId,
                    employeeId
            );

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password", e);
        }
    }

    @Override
    @Transactional
    public AuthenticationResponse register(UserRegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateException("Email đã đăng ký: " + request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getEmail().split("@")[0]);
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.EMPLOYEE);
        user.setStatus(UserStatus.INACTIVE);

        com.example.qlns.Entity.Employee emp = new com.example.qlns.Entity.Employee();
        emp.setFullName(request.getFullName());
        emp.setEmail(request.getEmail());
        emp.setPhone(request.getPhone());
        emp.setAddress(request.getAddress());
        emp.setGender(request.getGender());
        emp.setDateOfBirth(request.getDateOfBirth());
        emp.setPosition("Nhân viên mới");
        emp.setStatus(com.example.qlns.Enum.EmployeeStatus.ACTIVE);
        
        com.example.qlns.Entity.Employee savedEmp = empRepo.save(emp);
        user.setEmployeeId(savedEmp.getId());

        User savedUser = userRepository.save(user);

        UserDetailsImpl userDetails = new UserDetailsImpl(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getPasswordHash(),
                savedUser.getRole(),
                savedUser.getStatus()
        );

        String token = jwtService.generateToken(userDetails);

        return new AuthenticationResponse(
                token,
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                null,
                savedUser.getEmployeeId()
        );
    }

    @Override
    public boolean validateToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            UserDetailsImpl userDetails = new UserDetailsImpl(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getRole(),
                    user.getStatus()
            );

            return jwtService.validateToken(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public String changePassword(String username, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadCredentialsException("New password and confirm password do not match");
        }

        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new BadCredentialsException("New password must be different from old password");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Old password is incorrect");
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(encodedPassword);
        userRepository.save(user);

        return "Password changed successfully";
    }
}
