package com.tus.tpt.controller;

import com.tus.tpt.dto.player.PlayerDto;
import com.tus.tpt.dto.session.CreateNewTrainingSession;
import com.tus.tpt.dto.session.TrainingSessionResponse;
import com.tus.tpt.dto.upload.PlayerPerformanceResponse;
import com.tus.tpt.dto.upload.UploadPlayerPerformance;
import com.tus.tpt.model.TrainingSession;
import com.tus.tpt.model.TrainingType;
import com.tus.tpt.service.PlayerPerformanceService;
import com.tus.tpt.service.TrainingSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;
    private final PlayerPerformanceService playerPerformanceService;

    public TrainingSessionController(TrainingSessionService trainingSessionService, PlayerPerformanceService playerPerformanceService) {
        this.trainingSessionService = trainingSessionService;
        this.playerPerformanceService = playerPerformanceService;
    }

    @GetMapping
    public List<TrainingSessionResponse> getAllTrainingSessions() {
        return trainingSessionService.findAllTrainingSessions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingSessionResponse> getTrainingSession(@PathVariable Long id) {
        return trainingSessionService.findTrainingSessionById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TrainingSessionResponse> createSession(
            @Valid @RequestBody CreateNewTrainingSession request
    ) {

        TrainingSession trainingSession = new TrainingSession(
                LocalDateTime.parse(request.datetime()),
                TrainingType.valueOf(request.type()),
                request.duration(),
                new java.util.HashSet<>()
        );

        return ResponseEntity.ok(trainingSessionService.createTrainingSession(trainingSession));
    }

    @GetMapping("/{sessionId}/available")
    public List<PlayerDto> getAvailablePlayers(@PathVariable Long sessionId) {
        return trainingSessionService.getAvailablePlayersForSession(sessionId);
    }
    @PostMapping("/{id}/players/{username}")
    public ResponseEntity<TrainingSessionResponse> addPlayerToSession(
            @PathVariable Long id,
            @PathVariable String username
    ) {
        return ResponseEntity.ok(trainingSessionService.addPlayerToTrainingSession(username, id));
    }

    @PostMapping("/{sessionId}/performance")
    public ResponseEntity<?> uploadPlayerData(
            @PathVariable Long sessionId,
            @Valid @RequestBody UploadPlayerPerformance request
    ) {
        playerPerformanceService.uploadPlayerData(sessionId, request);
        return ResponseEntity.ok(Map.of("message", "Player data uploaded successfully"));
    }

    @GetMapping("/{sessionId}/performance")
    public List<PlayerPerformanceResponse> getPerformanceForSession(@PathVariable Long sessionId) {
        return playerPerformanceService.getPerformanceForSession(sessionId);
    }
}
