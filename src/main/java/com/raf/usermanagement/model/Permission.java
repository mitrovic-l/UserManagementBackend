package com.raf.usermanagement.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Permission {
    @Id
    @GeneratedValue
    @Column
    private Integer id;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "user_permission",
            joinColumns = @JoinColumn(name = "permissionid", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "userid", referencedColumnName = "id")
    )
    List<User> users;
}
