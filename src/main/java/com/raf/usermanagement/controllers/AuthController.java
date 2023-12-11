package com.raf.usermanagement.controllers;

import com.raf.usermanagement.model.User;
import com.raf.usermanagement.requests.LoginRequest;
import com.raf.usermanagement.responses.LoginResponse;
import com.raf.usermanagement.services.UserService;
import com.raf.usermanagement.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginInformation){
        try{
            authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(loginInformation.getEmail(), loginInformation.getPassword()));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(401).body("Pogresno uneti podaci.");
        }
        User user = userService.loadUserByEmail(loginInformation.getEmail());
        //Set<Permission> permissions = new HashSet<>(user.getRoles());
        return ResponseEntity.ok( new LoginResponse(jwtUtil.generateToken(user.getEmail(), user.getRoles())));
    }
}
