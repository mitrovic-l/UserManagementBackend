package com.raf.usermanagement.services;

import com.raf.usermanagement.model.Permission;
import com.raf.usermanagement.model.RoleType;
import com.raf.usermanagement.model.User;
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

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public User updateUser(User user){
        User existingUser = getUser(user.getEmail());
        existingUser.setFirstname(user.getFirstname());
        existingUser.setLastname(user.getLastname());
        return this.userRepository.save(existingUser);
    }
}
