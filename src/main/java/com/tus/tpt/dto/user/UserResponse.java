package com.tus.tpt.dto.user;

import com.tus.tpt.model.Role;

public record UserResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        Role role
) {}