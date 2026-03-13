package com.tus.tpt.service;

import com.tus.tpt.dao.PlayerPerformanceRepository;
import com.tus.tpt.dto.player.PlayerSessionResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PlayerSessionService {

    private final PlayerPerformanceRepository playerPerformanceRepository;

    public PlayerSessionService(PlayerPerformanceRepository playerPerformanceRepository) {
        this.playerPerformanceRepository = playerPerformanceRepository;
    }

    public List<PlayerSessionResponse> getSessionsForLoggedInPlayer(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        return playerPerformanceRepository.findByPlayer_UsernameIgnoreCaseOrderBySession_DatetimeDesc(username)
                .stream()
                .map(pp -> new PlayerSessionResponse(
                        pp.getSession().getDatetime(),
                        pp.getSession().getType(),
                        pp.getSession().getDuration(),
                        pp.getTotalDistance(),
                        pp.getHighIntensityDistance(),
                        pp.getTopSpeed(),
                        pp.getEffortRating()
                ))
                .toList();
    }
}