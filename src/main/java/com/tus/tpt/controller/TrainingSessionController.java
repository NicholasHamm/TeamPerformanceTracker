package com.tus.tpt.controller;

import com.tus.tpt.dto.CreateNewTrainingSession;
import com.tus.tpt.dto.TrainingSessionResponse;
import com.tus.tpt.model.TrainingSession;
import com.tus.tpt.model.TrainingType;
import com.tus.tpt.service.TrainingSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;

    public TrainingSessionController(TrainingSessionService trainingSessionService) {
        this.trainingSessionService = trainingSessionService;
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
            @RequestBody CreateNewTrainingSession request
    ) {

        TrainingSession trainingSession = new TrainingSession(
                LocalDateTime.parse(request.datetime()),
                TrainingType.valueOf(request.type()),
                request.duration(),
                new java.util.HashSet<>()
        );

        return ResponseEntity.ok(trainingSessionService.createTrainingSession(trainingSession));
    }

    @PostMapping("/{id}/players/{username}")
    public ResponseEntity<TrainingSessionResponse> addPlayerToSession(
            @PathVariable Long id,
            @PathVariable String username
    ) {
        return ResponseEntity.ok(trainingSessionService.addPlayerToTrainingSession(username, id));
    }
}
