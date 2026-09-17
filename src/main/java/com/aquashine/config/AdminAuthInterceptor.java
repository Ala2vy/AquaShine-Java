package com.aquashine.config;

import com.aquashine.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    public AdminAuthInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Not logged in\"}");
            return false;
        }

        Long userId = ((Number) session.getAttribute("userId")).longValue();
        var user = userRepository.findById(userId);
        if (user.isEmpty() || !"ADMIN".equals(user.get().getRole())) {
            response.setStatus(403);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Admin access required\"}");
            return false;
        }
        return true;
    }
}