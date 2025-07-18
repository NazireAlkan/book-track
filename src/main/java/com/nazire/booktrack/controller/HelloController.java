package com.nazire.booktrack.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String hello() {
        return "Hello, authenticated user!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String helloAdmin() {
        return "Hello, admin!";
    }
}
