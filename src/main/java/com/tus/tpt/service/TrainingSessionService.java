package com.tus.tpt.service;

import com.tus.tpt.dao.PlayerPerformanceRepository;
import com.tus.tpt.dao.TrainingSessionRepository;
import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.dto.player.PlayerDto;
import com.tus.tpt.dto.session.SessionPerformanceResponse;
import com.tus.tpt.dto.session.TrainingSessionResponse;
import com.tus.tpt.dto.upload.PlayerPerformanceResponse;
import com.tus.tpt.model.PlayerPerformance;
import com.tus.tpt.model.Role;
import com.tus.tpt.model.TrainingSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.time.LocalDateTime.now;

@Service
public class TrainingSessionService {

    private static final int MIN_DURATION = 10;
    private static final int MAX_DURATION = 300;

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

    public SessionPerformanceResponse getUploadedDataForSession(Long sessionId) {

        if (sessionId == null || !trainingSessionRepo.existsById(sessionId)) {
            throw new IllegalArgumentException("Session not found");
        }

        List<PlayerPerformanceResponse> performances =
                playerPerformanceRepo.findBySessionId(sessionId)
                        .stream()
                        .map(performance -> new PlayerPerformanceResponse(
                                performance.getPlayer().getId(),
                                performance.getPlayer().getFirstName() + " " + performance.getPlayer().getLastName(),
                                performance.getTotalDistance(),
                                performance.getDistancePerMin(),
                                performance.getHighIntensityDistance(),
                                performance.getTopSpeed(),
                                performance.getEffortRating()
                        ))
                        .toList();

        return new SessionPerformanceResponse(sessionId, performances);
    }

    public List<PlayerDto> getAvailablePlayersForSession(Long sessionId) {
        if (!trainingSessionRepo.existsById(sessionId)) {
            throw new IllegalArgumentException("Session not found");
        }

        Set<Long> usedPlayerIds = playerPerformanceRepo.findBySessionId(sessionId)
                .stream()
                .map(pp -> pp.getPlayer().getId())
                .collect(java.util.stream.Collectors.toSet());

        return userRepo.findByRole(Role.PLAYER).stream()
                .filter(player -> !usedPlayerIds.contains(player.getId()))
                .map(player -> new PlayerDto(
                        player.getId(),
                        player.getFirstName(),
                        player.getLastName()
                ))
                .toList();
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

        if (trainingSession.getDuration() < MIN_DURATION || trainingSession.getDuration() >= MAX_DURATION) {
            throw new IllegalArgumentException("Duration must be between "+ MIN_DURATION +" and "+ MAX_DURATION +" minutes");
        }
    }

    private TrainingSessionResponse toResponse(TrainingSession session) {
        Set<PlayerDto> players = (session.getPerformances() == null ? java.util.Set.<PlayerPerformance>of() : session.getPerformances())
                .stream()
                .map(PlayerPerformance::getPlayer)
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