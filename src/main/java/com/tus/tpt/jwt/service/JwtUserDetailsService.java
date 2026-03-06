package com.tus.tpt.jwt.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        if ("mary".equals(username)) {
            return User.builder()
                    .username("mary")
                    .password("$2a$12$yUSsdB0gbxhR0H4NZVO2TOVFvpJ4GzRWRKRaBgIX1kF5SuWgviZuu") // mary123
                    .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .build();
        }

        throw new UsernameNotFoundException(
                "User not found with username: " + username);
    }
}