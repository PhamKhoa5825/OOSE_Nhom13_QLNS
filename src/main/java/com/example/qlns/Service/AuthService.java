package com.example.qlns.Service;

import com.example.qlns.DTO.Request.AuthenticationRequest;
import com.example.qlns.DTO.Request.ChangePasswordRequest;
import com.example.qlns.DTO.Request.UserRegistrationRequest;
import com.example.qlns.DTO.Response.AuthenticationResponse;
import org.springframework.security.core.AuthenticationException;

public interface AuthService {
    AuthenticationResponse login(AuthenticationRequest request) throws AuthenticationException;
    AuthenticationResponse register(UserRegistrationRequest request);
    boolean validateToken(String token);
    String changePassword(String username, ChangePasswordRequest request);
}
