package com.tus.tpt.dto.analytics;

public record SessionAveragesResponse(
        Long sessionId,
        Double averageTotalDistance,
        Double averageDistancePerMin,
        Double averageHighIntensityDistance,
        Double averageTopSpeed,
        Double averageEffortRating
) {}