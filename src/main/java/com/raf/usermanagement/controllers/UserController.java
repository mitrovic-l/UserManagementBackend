package com.raf.usermanagement.controllers;

import com.raf.usermanagement.model.RoleType;
import com.raf.usermanagement.services.UserService;
import com.raf.usermanagement.utils.JwtUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUsers(){
        System.out.println("-------------> Context: "+SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(RoleType.can_read)){
            return ResponseEntity.ok(userService.findAll());
        } else {
            return ResponseEntity.status(401).body("Nemate dozvolu za pregled korisnika.");
        }
    }

}
