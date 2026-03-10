package com.tus.tpt.dto;

public record PlayerPerformanceResponse(
        Long id,
        Long playerId,
        String playerName,
        Long sessionId,
        Double totalDistance,
        Double distancePerMin,
        Double highIntensityDistance,
        Double topSpeed,
        Integer effortRating
) {}