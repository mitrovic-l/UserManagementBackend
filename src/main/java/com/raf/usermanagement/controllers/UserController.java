package com.raf.usermanagement.controllers;

import com.raf.usermanagement.model.Permission;
import com.raf.usermanagement.model.RoleType;
import com.raf.usermanagement.model.User;
import com.raf.usermanagement.services.UserService;
import com.raf.usermanagement.utils.JwtUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id){
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(RoleType.can_delete)){
            Optional<User> optionalUser = userService.findById(id);
            System.out.println(optionalUser);
            if (optionalUser.isPresent()){
                userService.deleteById(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(403).body("Nemate dozvolu da brisete korisnike.");
    }

    @GetMapping(value = "/roles")
    public List<RoleType> getRoles(){
        return (List<RoleType>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }
    @GetMapping(value = "/roles/{id}")
    public List<Permission> getPermissionsById(@PathVariable Integer id){
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(RoleType.can_read)) {
            return this.userService.permissions(id);
        }
        return null;
    }

    @GetMapping(value = "/find/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User findUser(@PathVariable String email){
        return this.userService.getUser(email);
    }


}
