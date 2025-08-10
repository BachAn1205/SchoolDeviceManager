package com.example.cuoikyjavaa.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String redirectUrl = "/";
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/users"; // Hoặc dashboard của admin
                break;
            } else if (authorityName.equals("ROLE_GIANGVIEN")) {
                redirectUrl = "/giangvien/dashboard";
                break;
            } else if (authorityName.equals("ROLE_SINHVIEN")) {
                redirectUrl = "/sinhvien/find_equipment"; // Hoặc dashboard của sinh viên
                break;
            } else if (authorityName.equals("ROLE_NHANVIEN")) {
                redirectUrl = "/nhanvien/my_requests"; // Hoặc dashboard của nhân viên
                break;
            } else if (authorityName.equals("ROLE_KYTHUATVIEN")) {
                redirectUrl = "/kythuatvien/equipment_management"; // Hoặc dashboard của kỹ thuật viên
                break;
            }
        }
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}