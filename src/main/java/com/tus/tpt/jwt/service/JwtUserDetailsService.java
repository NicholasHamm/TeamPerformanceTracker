package com.tus.tpt.jwt.service;

import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

//    @Override
//    public UserDetails loadUserByUsername(String username)
//            throws UsernameNotFoundException {
//
//        if ("mary".equals(username)) {
//            return User.builder()
//                    .username("mary")
//                    .password("$2a$12$yUSsdB0gbxhR0H4NZVO2TOVFvpJ4GzRWRKRaBgIX1kF5SuWgviZuu") // mary123
//                    .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")))
//                    .build();
//        }
//
//        throw new UsernameNotFoundException(
//                "User not found with username: " + username);
//    }

    private final UserRepository userRepo;

    public JwtUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRepo.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String role = String.valueOf(appUser.getRole()).toUpperCase();
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return org.springframework.security.core.userdetails.User
                .withUsername(appUser.getUsername())
                .password(appUser.getPassword())   // BCrypt hash from DB
                .authorities(authority)
                .build();
    }
}