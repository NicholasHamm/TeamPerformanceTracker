package com.tus.tpt.controller;

import com.tus.tpt.dto.player.PlayerSessionResponse;
import com.tus.tpt.service.PlayerSessionService;
import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/player/sessions")
public class PlayerSessionController {

    private final PlayerSessionService playerSessionService;

    public PlayerSessionController(PlayerSessionService playerSessionService) {
        this.playerSessionService = playerSessionService;
    }

    @GetMapping
    public ResponseEntity<List<PlayerSessionResponse>> getMySessions(Principal principal) {
        String username = principal.getName();
        List<PlayerSessionResponse> sessions =
                playerSessionService.getSessionsForLoggedInPlayer(username);
        return ResponseEntity.ok(sessions);
    }
}