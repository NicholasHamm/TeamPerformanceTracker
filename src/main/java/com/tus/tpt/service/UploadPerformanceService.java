package com.tus.tpt.service;

import com.tus.tpt.dao.PlayerPerformanceRepository;
import com.tus.tpt.dao.TrainingSessionRepository;
import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.dto.upload.PlayerPerformanceResponse;
import com.tus.tpt.dto.upload.UploadPlayerPerformance;
import com.tus.tpt.model.PlayerPerformance;
import com.tus.tpt.model.TrainingSession;
import com.tus.tpt.model.User;
import com.tus.tpt.model.Role;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UploadPerformanceService {

    private final PlayerPerformanceRepository performanceRepository;
    private final UserRepository userRepository;
    private final TrainingSessionRepository sessionRepository;

    public UploadPerformanceService(PlayerPerformanceRepository performanceRepository,
                                    UserRepository userRepository,
                                    TrainingSessionRepository sessionRepository) {
        this.performanceRepository = performanceRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public PlayerPerformanceResponse uploadPlayerData(Long sessionId, UploadPlayerPerformance request) {
        User player = userRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Please select an existing player"));

        if (player.getRole() != Role.PLAYER) {
            throw new IllegalArgumentException("User is not a player");
        }

        TrainingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Training session not found"));

        if (performanceRepository.existsByPlayerIdAndSessionId(request.getPlayerId(), sessionId)) {
            throw new IllegalArgumentException("Data already exists for this player in this session");
        }
        PlayerPerformance performance = getPlayerPerformance(request, session, player);

        try {
            performanceRepository.save(performance);
            return toResponse(performance);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Data already exists for this player in this session");
        }
    }
    
    public PlayerPerformanceResponse updatePlayerData(Long sessionId, UploadPlayerPerformance request) {

        PlayerPerformance performance = performanceRepository
                .findByPlayerIdAndSessionId(request.getPlayerId(), sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Performance data not found"));

        TrainingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Training session not found"));

        if (request.getHighIntensityDistance() > request.getTotalDistance()) {
            throw new IllegalArgumentException("High intensity distance cannot exceed total distance");
        }

        double distancePerMin = request.getTotalDistance() / session.getDuration();

        performance.setTotalDistance(request.getTotalDistance());
        performance.setHighIntensityDistance(request.getHighIntensityDistance());
        performance.setTopSpeed(request.getTopSpeed());
        performance.setEffortRating(request.getEffortRating());
        performance.setDistancePerMin(distancePerMin);

        performanceRepository.save(performance);

        return toResponse(performance);
    }
    
    public void deletePlayerPerformance(Long sessionId, Long playerId) {

        PlayerPerformance performance = performanceRepository
                .findByPlayerIdAndSessionId(playerId, sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Performance data not found"));

        performanceRepository.delete(performance);
    }

    private static PlayerPerformance getPlayerPerformance(UploadPlayerPerformance request, TrainingSession session, User player) {
        if (request.getHighIntensityDistance() > request.getTotalDistance()) {
            throw new IllegalArgumentException("High intensity distance cannot exceed total distance");
        }
        double distancePerMin = request.getTotalDistance() / session.getDuration();

        return new PlayerPerformance(
                player,
                session,
                request.getTotalDistance(),
                distancePerMin,
                request.getHighIntensityDistance(),
                request.getTopSpeed(),
                request.getEffortRating()
        );
    }

    private PlayerPerformanceResponse toResponse(PlayerPerformance performance) {
        return new PlayerPerformanceResponse(
                performance.getPlayer().getId(),
                performance.getPlayer().getFirstName() + " " + performance.getPlayer().getLastName(),
                performance.getTotalDistance(),
                performance.getDistancePerMin(),
                performance.getHighIntensityDistance(),
                performance.getTopSpeed(),
                performance.getEffortRating()
        );
    }
}