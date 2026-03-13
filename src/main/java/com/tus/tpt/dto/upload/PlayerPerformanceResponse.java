package com.tus.tpt.dto.upload;

public record PlayerPerformanceResponse(
        Long playerId,
        String playerName,
        Double totalDistance,
        Double distancePerMin,
        Double highIntensityDistance,
        Double topSpeed,
        Integer effortRating
) {}