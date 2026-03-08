package com.tus.tpt.jwt.controller;

import com.tus.tpt.jwt.model.JwtResponse;
import com.tus.tpt.jwt.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class JwtAuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public JwtAuthenticationController(AuthenticationManager authenticationManager,
                                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @GetMapping("/login")
    public ResponseEntity<JwtResponse> loginWithQueryParams(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        return authenticateAndRespond(username, password);
    }

    private ResponseEntity<JwtResponse> authenticateAndRespond(String username, String password) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                username,
                                password
                        )
                );

        String token = jwtService.generateToken(
                (org.springframework.security.core.userdetails.UserDetails)
                        authentication.getPrincipal()
        );

        return ResponseEntity.ok(new JwtResponse(token));
    }
}