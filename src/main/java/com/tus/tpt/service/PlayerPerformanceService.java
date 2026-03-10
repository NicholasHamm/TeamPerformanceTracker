package com.tus.tpt.service;

import com.tus.tpt.dao.PlayerPerformanceRepository;
import com.tus.tpt.dao.TrainingSessionRepository;
import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.dto.PlayerPerformanceResponse;
import com.tus.tpt.dto.UploadPlayerPerformance;
import com.tus.tpt.model.PlayerPerformance;
import com.tus.tpt.model.TrainingSession;
import com.tus.tpt.model.User;
import com.tus.tpt.model.Role;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerPerformanceService {

    private final PlayerPerformanceRepository performanceRepository;
    private final UserRepository userRepository;
    private final TrainingSessionRepository sessionRepository;

    public PlayerPerformanceService(PlayerPerformanceRepository performanceRepository,
                                    UserRepository userRepository,
                                    TrainingSessionRepository sessionRepository) {
        this.performanceRepository = performanceRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public void uploadPlayerData(Long sessionId, UploadPlayerPerformance request) {
        User player = userRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        if (player.getRole() != Role.PLAYER) {
            throw new IllegalArgumentException("Player not found");
        }

        TrainingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (performanceRepository.existsByPlayerIdAndSessionId(request.getPlayerId(), sessionId)) {
            throw new IllegalArgumentException("Data already exists for this player in this session");
        }

        validateBusinessRanges(request);

        PlayerPerformance performance = new PlayerPerformance(
                player,
                session,
                request.getTotalDistance(),
                request.getDistancePerMin(),
                request.getHighIntensityDistance(),
                request.getTopSpeed(),
                request.getEffortRating()
        );

        try {
            performanceRepository.save(performance);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Data already exists for this player in this session");
        }
    }

    private void validateBusinessRanges(UploadPlayerPerformance request) {
        if (request.getHighIntensityDistance() > request.getTotalDistance()) {
            throw new IllegalArgumentException("High intensity distance cannot exceed total distance");
        }

        if (request.getDistancePerMin() > 300) {
            throw new IllegalArgumentException("Distance per min is unrealistic");
        }

        if (request.getTotalDistance() > 25000) {
            throw new IllegalArgumentException("Total distance is unrealistic");
        }

        if (request.getHighIntensityDistance() > 10000) {
            throw new IllegalArgumentException("High intensity distance is unrealistic");
        }
    }

    public List<PlayerPerformanceResponse> getPerformanceForSession(Long sessionId) {
        return performanceRepository.findBySessionId(sessionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PlayerPerformanceResponse toResponse(PlayerPerformance performance) {
        return new PlayerPerformanceResponse(
                performance.getId(),
                performance.getPlayer().getId(),
                performance.getPlayer().getFirstName() + " " + performance.getPlayer().getLastName(),
                performance.getSession().getId(),
                performance.getTotalDistance(),
                performance.getDistancePerMin(),
                performance.getHighIntensityDistance(),
                performance.getTopSpeed(),
                performance.getEffortRating()
        );
    }
}