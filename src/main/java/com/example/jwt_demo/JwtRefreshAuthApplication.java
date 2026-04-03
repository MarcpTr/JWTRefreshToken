package com.example.jwt_demo;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class JwtRefreshAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtRefreshAuthApplication.class, args);
	}
}
