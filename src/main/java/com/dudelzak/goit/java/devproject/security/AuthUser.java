package com.dudelzak.goit.java.devproject.security;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class AuthUser implements UserDetails {

    private final User user;

    @Override
    public @NonNull String getUsername() { return user.getUsername(); }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        // Return a list of roles or authorities assigned to the user.
        return List.of();
    }
}
