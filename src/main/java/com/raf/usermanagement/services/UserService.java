package com.raf.usermanagement.services;

import com.raf.usermanagement.model.Permission;
import com.raf.usermanagement.model.RoleType;
import com.raf.usermanagement.model.User;
import com.raf.usermanagement.repositories.PermissionRepository;
import com.raf.usermanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private PermissionRepository permissionRepository;

    @Autowired
    public UserService(UserRepository userRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(username);
        if (user == null){
            throw new UsernameNotFoundException("Email: " + username + " nije povezan ni sa jednim korisnikom.");
        }
        //TODO: proveriti da li moze tj. da li radi ovako ili treba dodati .getAuthority()
        Set<RoleType> roles = new TreeSet<>();
        for (Permission permission : user.getRoles()){
            roles.add(permission.getRole());
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), roles);
    }

    public UserDetails loadUserDetailsByEmail(String email) throws UsernameNotFoundException{
        User user = this.userRepository.findByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException("Email: " + email + " nije povezan ni sa jednim korisnikom.");
        }
        Set<RoleType> roles = new TreeSet<>();
        for (Permission permission : user.getRoles()){
            roles.add(permission.getRole());
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), roles);
    }
    public User loadUserByEmail(String email) throws UsernameNotFoundException{
        User user = this.userRepository.findByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException("Email: " + email + " nije povezan ni sa jednim korisnikom.");
        }
        return user;
    }
    public List<User> findAll(){
        return this.userRepository.findAll();
    }
    public Optional<User> findById(Integer id){
        return this.userRepository.findById(id);
    }
    public void deleteById(Integer id){
        this.userRepository.deleteById(id);
    }
    public User save(User user){
        return this.userRepository.save(user);
    }
    public boolean isEmailAvailable(String email){
        User check = this.userRepository.findByEmail(email);
        if (check == null)
            return true;
        return false;
    }
    public User createNewUser(User user) throws UsernameNotFoundException{
        if (!isEmailAvailable(user.getEmail())){
            throw new UsernameNotFoundException("Korisnik sa takvim mailom vec postoji.");
        }
        else {
            return this.userRepository.save(user);
        }
    }
    public User getUser(String email){
        return this.userRepository.findByEmail(email);
    }
    public User updateUser(User user, Integer id){
        return this.userRepository.save(user);
    }
    public Optional<User> getOptionalUser(Integer id){
        return this.userRepository.findById(id);
    }
    public List<Permission> permissions(Integer id){
        Optional<User> user = findById(id);
        if (user.isPresent()){
            return user.get().getRoles();
        }
        throw new UsernameNotFoundException("Korisnik sa id-em " + id + " nije pronadjen.");
    }
    public List<Permission> allPermissions(){
        return this.permissionRepository.findAll();
    }
}
