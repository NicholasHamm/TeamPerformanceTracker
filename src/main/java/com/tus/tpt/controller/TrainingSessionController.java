package com.tus.tpt.controller;

import com.tus.tpt.dto.player.PlayerDto;
import com.tus.tpt.dto.session.CreateNewTrainingSession;
import com.tus.tpt.dto.session.SessionPerformanceResponse;
import com.tus.tpt.dto.session.TrainingSessionResponse;
import com.tus.tpt.dto.upload.PlayerPerformanceResponse;
import com.tus.tpt.dto.upload.UploadPlayerPerformance;
import com.tus.tpt.model.TrainingSession;
import com.tus.tpt.model.TrainingType;
import com.tus.tpt.service.UploadPerformanceService;
import com.tus.tpt.service.TrainingSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;
    private final UploadPerformanceService playerPerformanceService;

    public TrainingSessionController(TrainingSessionService trainingSessionService, UploadPerformanceService playerPerformanceService) {
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
        if (request.type() == null || request.type().isBlank()) {
            throw new IllegalArgumentException("Training Type must be selected");
        }

        TrainingSession trainingSession = new TrainingSession(
                LocalDateTime.parse(request.datetime()),
                TrainingType.valueOf(request.type()),
                request.duration()
        );

        return ResponseEntity.ok(trainingSessionService.createTrainingSession(trainingSession));
    }

    @GetMapping("/{sessionId}/available")
    public List<PlayerDto> getAvailablePlayers(@PathVariable Long sessionId) {
        return trainingSessionService.getAvailablePlayersForSession(sessionId);
    }

    @PostMapping("/{sessionId}/performance")
    public ResponseEntity<PlayerPerformanceResponse> uploadPlayerData(
            @PathVariable Long sessionId,
            @Valid @RequestBody UploadPlayerPerformance request
    ) {
        PlayerPerformanceResponse response = playerPerformanceService.uploadPlayerData(sessionId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sessionId}/performance")
    public SessionPerformanceResponse getPerformanceForSession(@PathVariable Long sessionId) {
        return trainingSessionService.getUploadedDataForSession(sessionId);
    }
}
