package com.example.jwt_demo.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
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
    public String whoAmI() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return "No hay usuario autenticado";

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return "Usuario: " + user.getUsername() + ", Rol: " + user.getRole().name();
        }
        return "Principal no es un User válido";
    }
}
