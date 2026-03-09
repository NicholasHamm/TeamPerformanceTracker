package com.tus.tpt.config;

import com.tus.tpt.jwt.config.JwtAuthenticationEntryPoint;
import com.tus.tpt.jwt.security.JwtAuthenticationFilter;
import com.tus.tpt.jwt.security.JwtUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint entryPoint;
    private final JwtAuthenticationFilter authTokenFilter;
    private final JwtUserDetailsService jwtUserDetailsService;

    public SecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler,
                          JwtAuthenticationFilter authTokenFilter,
                          JwtUserDetailsService jwtUserDetailsService) {
        this.entryPoint = unauthorizedHandler;
        this.authTokenFilter = authTokenFilter;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            // CSRF disabled because authentication is stateless JWT-based.
            // The application does not use cookies or HTTP sessions for authentication.
            http
                    .csrf(csrf -> csrf.disable())
                    .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(
                                    "/",
                                    "/index.html",
                                    "/login",
                                    "/styles.css",
                                    "/js/**",
                                    "/error",
                                    "/favicon.ico",
                                    "/auth/**"
                            ).permitAll()
                            .requestMatchers("/api/users/**").hasRole("ADMIN")
                            .anyRequest().authenticated()
                    )
                    .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build SecurityFilterChain", e);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(jwtUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }
}