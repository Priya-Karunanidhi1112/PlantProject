package com.example.planting.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String redirectUrl = "/";

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if (auth.getAuthority().equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
                break;
            }
        }

        System.out.println("Authorities: " + authentication.getAuthorities());
        System.out.println("Redirecting to: " + redirectUrl);

        response.sendRedirect(redirectUrl);
    }
}
