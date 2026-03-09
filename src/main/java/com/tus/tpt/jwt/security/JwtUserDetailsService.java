package com.tus.tpt.jwt.security;

import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public JwtUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRepo.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String role = appUser.getRole().name();
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return org.springframework.security.core.userdetails.User
                .withUsername(appUser.getUsername())
                .password(appUser.getPassword())
                .authorities(authority)
                .build();
    }
}