package com.tus.tpt.jwt.controller;

import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.jwt.model.JwtRequest;
import com.tus.tpt.jwt.model.JwtResponse;
import com.tus.tpt.jwt.security.JwtUserDetailsService;
import com.tus.tpt.jwt.service.JwtService;
import com.tus.tpt.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class JwtAuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public JwtAuthenticationController(AuthenticationManager authenticationManager,
                                       JwtService jwtService,
                                       JwtUserDetailsService userDetailsService,
                                       UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow();

        return ResponseEntity.ok(
                new JwtResponse(token, user.getRole().name())
        );
    }
}