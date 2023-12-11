package com.raf.usermanagement.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    @Column
    private String firstname;
    @Column
    private String lastname;
    @Column
    private String email;
    @Column
    private String password;

    @JsonIgnore
    @Cascade(org.hibernate.annotations.CascadeType.PERSIST)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_permission",
            joinColumns = @JoinColumn(name = "userid", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permissionid", referencedColumnName = "id")
    )
    private List<Permission> roles = new ArrayList<>();


//    private List<Permission> roles = new ArrayList<>();
}
