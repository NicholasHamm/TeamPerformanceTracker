package com.tus.tpt.service;

import com.tus.tpt.dao.PlayerPerformanceRepository;
import com.tus.tpt.dao.TrainingSessionRepository;
import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.dto.PlayerDto;
import com.tus.tpt.dto.TrainingSessionResponse;
import com.tus.tpt.model.Role;
import com.tus.tpt.model.TrainingSession;
import com.tus.tpt.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.time.LocalDateTime.now;

@Service
public class TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepo;
    private final UserRepository userRepo;
    private final PlayerPerformanceRepository playerPerformanceRepo;

    public TrainingSessionService(TrainingSessionRepository trainingSessionRepo, UserRepository userRepo, PlayerPerformanceRepository playerPerformanceRepo) {
        this.trainingSessionRepo = trainingSessionRepo;
        this.userRepo = userRepo;
        this.playerPerformanceRepo = playerPerformanceRepo;
    }

    public List<TrainingSessionResponse> findAllTrainingSessions() {
        return trainingSessionRepo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<TrainingSessionResponse> findTrainingSessionById(Long id) {
        if (id == null) return Optional.empty();
        return trainingSessionRepo.findById(id).map(this::toResponse);
    }

    public TrainingSessionResponse createTrainingSession(TrainingSession trainingSession) {
        validateTrainingSession(trainingSession);
        TrainingSession saved = trainingSessionRepo.save(trainingSession);
        return toResponse(saved);
    }

    public List<PlayerDto> getAvailablePlayersForSession(Long sessionId) {
        if (!trainingSessionRepo.existsById(sessionId)) {
            throw new IllegalArgumentException("Session not found");
        }

        Set<Long> usedPlayerIds = new java.util.HashSet<>(
                playerPerformanceRepo.findPlayerIdsBySessionId(sessionId)
        );

        return userRepo.findByRole(Role.PLAYER).stream()
                .filter(player -> !usedPlayerIds.contains(player.getId()))
                .map(player -> new PlayerDto(
                        player.getId(),
                        player.getFirstName(),
                        player.getLastName()
                ))
                .toList();
    }

    public TrainingSessionResponse addPlayerToTrainingSession(String username, Long trainingSessionId) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (trainingSessionId == null) {
            throw new IllegalArgumentException("Training session id is required");
        }

        TrainingSession session = trainingSessionRepo.findById(trainingSessionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Training session [id=" + trainingSessionId + "] not found"));

        User player = userRepo.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Player: " + username + " not found"));

        if (player.getRole() != Role.PLAYER) {
            throw new IllegalArgumentException("User: " + username + " is not a player");
        }

        if (session.getPlayers().contains(player)) {
            throw new IllegalArgumentException("Player: " + username + " already added to training session");
        }

        session.getPlayers().add(player);
        TrainingSession saved = trainingSessionRepo.save(session);
        return toResponse(saved);
    }

    public void validateTrainingSession(TrainingSession trainingSession) {
        if (trainingSession == null) {
            throw new IllegalArgumentException("Training session is required");
        }

        if (trainingSession.getDatetime() == null) {
            throw new IllegalArgumentException("Date & time are required");
        }

        if (trainingSession.getDatetime().isAfter(now())) {
            throw new IllegalArgumentException("Date cannot be in the future");
        }

        if (isSessionAvailable(trainingSession)) {
            throw new IllegalArgumentException("Training session overlaps with an existing session");
        }

        if (trainingSession.getType() == null) {
            throw new IllegalArgumentException("Training type is required");
        }

        if (trainingSession.getDuration() <= 10 || trainingSession.getDuration() > 300) {
            throw new IllegalArgumentException("Duration must be between 10 and 300 minutes");
        }
    }

    public boolean isSessionAvailable(TrainingSession trainingSession) {
        LocalDateTime start = trainingSession.getDatetime();
        LocalDateTime end = start.plusMinutes(trainingSession.getDuration());

        return trainingSessionRepo.findAll()
                .stream()
                .anyMatch(existing -> {
                    LocalDateTime existingStart = existing.getDatetime();
                    LocalDateTime existingEnd = existingStart.plusMinutes(existing.getDuration());

                    return start.isBefore(existingEnd) && end.isAfter(existingStart);
                });
    }

    private TrainingSessionResponse toResponse(TrainingSession session) {
        Set<PlayerDto> players = (session.getPlayers() == null ? java.util.Set.<User>of() : session.getPlayers())
                .stream()
                .map(u -> new PlayerDto(u.getId(), u.getFirstName(), u.getLastName()))
                .collect(java.util.stream.Collectors.toSet());

        return new TrainingSessionResponse(
                session.getId(),
                session.getDatetime(),
                session.getType(),
                session.getDuration(),
                players
        );
    }
}