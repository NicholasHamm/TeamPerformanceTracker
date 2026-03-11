package com.tus.tpt.dto.upload;

public record PlayerPerformanceResponse(
        Long playerId,
        String playerName,
        Long sessionId,
        Double totalDistance,
        Double distancePerMin,
        Double highIntensityDistance,
        Double topSpeed,
        Integer effortRating
) {}