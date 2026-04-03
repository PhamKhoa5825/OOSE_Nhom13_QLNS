package com.example.qlns.Controller;

import com.example.qlns.DTO.Request.AuthenticationRequest;
import com.example.qlns.DTO.Request.ChangePasswordRequest;
import com.example.qlns.DTO.Request.UserRegistrationRequest;
import com.example.qlns.DTO.Response.AuthenticationResponse;
import com.example.qlns.DTO.Response.ChangePasswordResponse;
import com.example.qlns.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles user login and registration endpoints
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/login
     * Authenticate user and return JWT token
     * 
     * @param request username and password
     * @return AuthenticationResponse with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/register
     * Register new user
     * 
     * @param request user registration details
     * @return AuthenticationResponse with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        AuthenticationResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/auth/validate
     * Validate JWT token (requires authentication)
     * 
     * @return success message
     */
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken() {
        // Get token from Authorization header
        String authHeader = ((org.springframework.web.context.request.RequestContextHolder.getRequestAttributes() != null)
            ? ((org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization")
            : null);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (authService.validateToken(token)) {
                return ResponseEntity.ok("Token is valid");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid");
    }

    /**
     * POST /api/auth/refresh
     * Refresh JWT token (requires authentication)
     * Can be used to get a new token before expiration
     * 
     * @return new JWT token
     */
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken() {
        return ResponseEntity.ok("Token refresh endpoint");
    }

    /**
     * POST /api/auth/change-password
     * Change user password (requires authentication)
     * 
     * @param request contains old password, new password, and confirm password
     * @return success message
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        // Get authenticated username from SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Call service to change password
        String message = authService.changePassword(username, request);
        
        return ResponseEntity.ok(new ChangePasswordResponse(message, true));
    }
}
