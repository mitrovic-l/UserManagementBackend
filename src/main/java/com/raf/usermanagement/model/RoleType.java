package com.raf.usermanagement.model;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType implements GrantedAuthority {

    can_create("can_create"),
    can_read("can_read"),
    can_update("can_update"),
    can_delete("can_delete");

    private final String role;
    RoleType(String role) {
        this.role = role;
    }
    @Override
    public String getAuthority() {
        return role;
    }
}
