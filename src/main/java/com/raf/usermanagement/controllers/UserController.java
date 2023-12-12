package com.raf.usermanagement.controllers;

import com.raf.usermanagement.model.RoleType;
import com.raf.usermanagement.model.User;
import com.raf.usermanagement.services.UserService;
import com.raf.usermanagement.utils.JwtUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.status(403).body("Nemate dozvolu za pregled korisnika.");
        }
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody User user){
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(RoleType.can_create)){
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            return ResponseEntity.ok(userService.createNewUser(user));
        } else {
            return ResponseEntity.status(403).body("Nemate dozvolu za kreiranje novog korisnika.");
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestBody User user){
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(RoleType.can_update)){
            User user1 = userService.getUser(user.getEmail());
            user.setPassword(user1.getPassword());
            return ResponseEntity.ok(userService.updateUser(user));
        } else {
            return ResponseEntity.status(403).body("Nemate dozvolu da menjate podatke korisnicima.");
        }
    }


}
