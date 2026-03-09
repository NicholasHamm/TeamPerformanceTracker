package com.tus.tpt.controller;

import com.tus.tpt.dto.PlayerDto;
import com.tus.tpt.service.PlayerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<PlayerDto> getPlayers() {
        return playerService.getAllPlayers();
    }
}