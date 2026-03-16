package com.tus.tpt.service;

import com.tus.tpt.dao.PlayerPerformanceRepository;
import com.tus.tpt.dao.TrainingSessionRepository;
import com.tus.tpt.dto.analytics.PlayerMetricResponse;
import com.tus.tpt.dto.analytics.SessionAveragesResponse;
import com.tus.tpt.model.PlayerPerformance;
import com.tus.tpt.model.TrainingSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionAnalyticsService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final PlayerPerformanceRepository playerPerformanceRepository;

    public SessionAnalyticsService(TrainingSessionRepository trainingSessionRepository,
                                   PlayerPerformanceRepository playerPerformanceRepository) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.playerPerformanceRepository = playerPerformanceRepository;
    }

    public SessionAveragesResponse getSessionAverages(Long sessionId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Training session not found"));

        List<PlayerPerformance> performances = playerPerformanceRepository.findBySessionId(session.getId());

        if (performances.isEmpty()) {
            throw new IllegalArgumentException("No player performance data found for this session");
        }

        double averageTotalDistance = performances.stream()
                .mapToDouble(PlayerPerformance::getTotalDistance)
                .average()
                .orElse(0.0);

        double averageDistancePerMin = performances.stream()
                .mapToDouble(PlayerPerformance::getDistancePerMin)
                .average()
                .orElse(0.0);

        double averageHighIntensityDistance = performances.stream()
                .mapToDouble(PlayerPerformance::getHighIntensityDistance)
                .average()
                .orElse(0.0);

        double averageTopSpeed = performances.stream()
                .mapToDouble(PlayerPerformance::getTopSpeed)
                .average()
                .orElse(0.0);

        double averageEffortRating = performances.stream()
                .mapToInt(PlayerPerformance::getEffortRating)
                .average()
                .orElse(0.0);

        return new SessionAveragesResponse(
                session.getId(),
                averageTotalDistance,
                averageDistancePerMin,
                averageHighIntensityDistance,
                averageTopSpeed,
                averageEffortRating
        );
    }

    public List<PlayerMetricResponse> getPlayerMetricBreakdown(Long sessionId, String metric) {
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Training session not found"));

        List<PlayerPerformance> performances = playerPerformanceRepository.findBySessionId(session.getId());

        if (performances.isEmpty()) {
            throw new IllegalArgumentException("No player performance data found for this session");
        }

        return performances.stream()
                .map(performance -> new PlayerMetricResponse(
                        performance.getPlayer().getFirstName() + " " + performance.getPlayer().getLastName(),
                        extractMetricValue(performance, metric)
                ))
                .toList();
    }

    private Double extractMetricValue(PlayerPerformance performance, String metric) {
        return switch (metric) {
            case "totalDistance" -> performance.getTotalDistance();
            case "distancePerMin" -> performance.getDistancePerMin();
            case "highIntensityDistance" -> performance.getHighIntensityDistance();
            case "topSpeed" -> performance.getTopSpeed();
            case "effortRating" -> performance.getEffortRating().doubleValue();
            default -> throw new IllegalArgumentException("Invalid metric selected");
        };
    }
}