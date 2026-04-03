package com.example.qlns.Security;

import com.example.qlns.Enum.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserStatusInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            String path = request.getRequestURI();
            
            // Allow access to waiting-approval, logout, and static resources
            if (path.equals("/waiting-approval") || path.equals("/login") || path.equals("/register") || 
                path.startsWith("/api/auth") || path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/images")) {
                return true;
            }
            
            if (userDetails.getStatus() == UserStatus.INACTIVE) {
                response.sendRedirect("/waiting-approval");
                return false;
            }
            
            if (userDetails.getStatus() == UserStatus.LOCKED) {
                response.sendRedirect("/login?error=locked");
                return false;
            }
        }
        
        return true;
    }
}
