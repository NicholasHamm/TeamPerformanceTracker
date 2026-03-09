package com.tus.tpt.service;

import com.tus.tpt.dao.TrainingSessionRepository;
import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.dto.PlayerDto;
import com.tus.tpt.dto.TrainingSessionResponse;
import com.tus.tpt.exception.DuplicateTrainingSessionException;
import com.tus.tpt.model.Role;
import com.tus.tpt.model.TrainingSession;
import com.tus.tpt.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
public class TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepo;
    private final UserRepository userRepo;

    public TrainingSessionService(TrainingSessionRepository trainingSessionRepo, UserRepository userRepo) {
        this.trainingSessionRepo = trainingSessionRepo;
        this.userRepo = userRepo;
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

        if (trainingSession.getType() == null) {
            throw new IllegalArgumentException("Training type is required");
        }

        if (trainingSession.getDuration() <= 10 || trainingSession.getDuration() > 300) {
            throw new IllegalArgumentException("Duration must be between 10 and 300 minutes");
        }

        if (trainingSessionRepo.existsByDatetimeAndType(
                trainingSession.getDatetime(),
                trainingSession.getType())) {
            throw new DuplicateTrainingSessionException();
        }
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