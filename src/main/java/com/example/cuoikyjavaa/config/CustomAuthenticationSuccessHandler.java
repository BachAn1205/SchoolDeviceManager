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
                redirectUrl = "/admin/dashboard";
                break;
            } else if (authorityName.equals("ROLE_GIANGVIEN") || authorityName.equals("ROLE_SINHVIEN")) {
                redirectUrl = "/giangvien/dashboard";
                break;
            } else if (authorityName.equals("ROLE_NHANVIEN")) {
                redirectUrl = "/nhanvien/dashboard"; // Hoặc dashboard của nhân viên
                break;
            } else if (authorityName.equals("ROLE_KYTHUATVIEN")) {
                redirectUrl = "/kythuatvien/dashboard"; // Hoặc dashboard của kỹ thuật viên
                break;
            }
        }
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}