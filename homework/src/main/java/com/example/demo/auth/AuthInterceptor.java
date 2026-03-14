package com.example.demo.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    public AuthInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }

        // 如果没有 RequireRole 注解，默认放行（或者你可以设置为默认拦截）
        if (requireRole == null) {
            return true;
        }

        // 获取 Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing or invalid token");
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Token expired or invalid");
            return false;
        }

        // 校验角色
        String role = jwtUtils.extractRole(token);
        User.Role userRole = User.Role.valueOf(role);
        User.Role[] allowedRoles = requireRole.value();

        if (allowedRoles.length > 0 && !Arrays.asList(allowedRoles).contains(userRole)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden: Insufficient permissions");
            return false;
        }

        // 将用户信息存入 request，方便后续使用
        request.setAttribute("userId", jwtUtils.extractUserId(token));
        request.setAttribute("username", jwtUtils.extractUsername(token));
        request.setAttribute("role", role);

        return true;
    }
}
