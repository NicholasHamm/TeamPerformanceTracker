package com.tus.tpt.service;

import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.dto.PlayerDto;
import com.tus.tpt.model.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final UserRepository userRepo;

    public PlayerService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<PlayerDto> getAllPlayers() {
        return userRepo.findByRole(Role.PLAYER)
                .stream()
                .map(this::toPlayerDto)
                .toList();
    }

    public Optional<PlayerDto> findPlayerByUsername(String username) {
        if (username == null || username.isBlank()) return Optional.empty();

        return userRepo.findByUsernameIgnoreCase(username.trim())
                .filter(u -> u.getRole() == Role.PLAYER)
                .map(this::toPlayerDto);
    }

    public Optional<PlayerDto> findPlayerById(Long id) {
        if (id == null) return Optional.empty();

        return userRepo.findById(id)
                .filter(u -> u.getRole() == Role.PLAYER)
                .map(this::toPlayerDto);
    }

    private PlayerDto toPlayerDto(com.tus.tpt.model.User user) {
        return new PlayerDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}