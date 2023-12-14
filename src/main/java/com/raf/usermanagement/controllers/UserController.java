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

import java.util.ArrayList;
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
    public ResponseEntity<?> createUser(@RequestBody User user, @RequestParam(value = "roleIDs", required = false) Integer[] ids){
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(RoleType.can_create)){
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            Permission p1 = new Permission();
            Permission p2 = new Permission();
            Permission p3 = new Permission();
            Permission p4 = new Permission();
            p1.setRole(RoleType.can_create);
            p2.setRole(RoleType.can_delete);
            p3.setRole(RoleType.can_update);
            p4.setRole(RoleType.can_read);
            List<Permission> permissions = new ArrayList<>();
            if (ids.length != 0) {
                for (Integer id : ids) {
                    if (id == 1) {
                        permissions.add(p1);
                    }
                    if (id == 2) {
                        permissions.add(p2);
                    }
                    if (id == 3) {
                        permissions.add(p3);
                    }
                    if (id == 4) {
                        permissions.add(p4);
                    }
                }
            }
            user.setRoles( permissions );
            return ResponseEntity.ok(userService.createNewUser(user));
        } else {
            return ResponseEntity.status(403).body("Nemate dozvolu za kreiranje novog korisnika.");
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestBody User user,@PathVariable("id") Integer userId, @RequestParam(value = "roleIDs", required = false) Integer[] ids){
        System.out.println("UPDATE USER U KONTROLERU");
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(RoleType.can_update)){
            Optional<User> user1 = userService.findById(userId);
            System.out.println(" ---------> naso korisinka:  " + user1.get().getEmail() + user1.get().getFirstname() + user1.get().getRoles()) ;
            user.setPassword(user1.get().getPassword());
            System.out.println("-----------> permisije");
            Permission create = new Permission();
            Permission p2 = new Permission();
            Permission p3 = new Permission();
            Permission p4 = new Permission();
            create.setRole(RoleType.can_create);
            p2.setRole(RoleType.can_delete);
            p3.setRole(RoleType.can_update);
            p4.setRole(RoleType.can_read);
            List<Permission> permissions = new ArrayList<>();
            System.out.println("-----------> kraj permisija");
            if (ids.length != 0) {
                for (Integer id : ids) {
                    if (id == 1) {
                        create.setRole(RoleType.can_create);
                        permissions.add(create);
                    }
                    if (id == 2) {
                        p2.setRole(RoleType.can_delete);
                        permissions.add(p2);
                    }
                    if (id == 3) {
                        p3.setRole(RoleType.can_update);
                        permissions.add(p3);
                    }
                    if (id == 4) {
                        p4.setRole(RoleType.can_read);
                        permissions.add(p4);
                    }
                }
            }
            user1.get().setRoles(permissions);
            user1.get().setFirstname(user.getFirstname());
            user1.get().setLastname(user.getLastname());
            System.out.println("Zovem save");
            return ResponseEntity.ok(userService.save(user1.get()));
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
