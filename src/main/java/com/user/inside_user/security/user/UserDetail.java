package com.user.inside_user.security.user;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.user.inside_user.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetail implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private Collection<GrantedAuthority> authority;

    public static UserDetail buildUserDetails(User user) {
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName());
        return new UserDetail(user.getId(), user.getEmail(), user.getPassword(), Arrays.asList(authority));
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authority;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.email;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GrantedAuthority getAuthority() {
        return (GrantedAuthority)((List)this.authority).get(0);
    }

    public void setAuthority(GrantedAuthority authority) {
        this.authority = Arrays.asList(authority);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserDetail(Long id, String email, String password, List<GrantedAuthority> list) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authority = list;
    }

    public UserDetail() {
    }
}

