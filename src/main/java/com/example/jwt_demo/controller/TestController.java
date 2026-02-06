package com.example.jwt_demo.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.jwt_demo.model.User;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/public")
    public String publicEndpoint() {
        return "Este endpoint es público, cualquiera puede acceder.";
    }

    @GetMapping("/user")
    public String userEndpoint() {
        return "Este endpoint requiere ROLE_USER o ROLE_ADMIN.";
    }

    @GetMapping("/admin")
    public String adminEndpoint() {
        return "Este endpoint requiere ROLE_ADMIN exclusivamente.";
    }

    @GetMapping("/whoami")
    public String whoAmI(@AuthenticationPrincipal User user) {
         return "Usuario: " + user.getUsername() +
           ", Rol: " + user.getRole().name();
    }
}
