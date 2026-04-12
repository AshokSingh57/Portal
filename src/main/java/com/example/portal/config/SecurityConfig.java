package com.example.portal.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/dashboard", "/admin", "/proxy/**")
                .excludePathPatterns("/", "/login", "/register", "/error", "/oauth2/callback");
    }

    static class AuthInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("authToken") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }

            String requestUri = request.getRequestURI();
            String contextPath = request.getContextPath();
            String path = requestUri.substring(contextPath.length());

            if (path.startsWith("/admin")) {
                String role = (String) session.getAttribute("userRole");
                if (!"Admin".equals(role)) {
                    response.sendRedirect(contextPath + "/dashboard");
                    return false;
                }
            }

            return true;
        }
    }
}
