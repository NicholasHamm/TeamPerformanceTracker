package com.tus.tpt.dto.upload;

import jakarta.validation.constraints.*;

public class UploadPlayerPerformance{

    @NotNull(message = "Player id is required")
    private Long playerId;

    @NotNull(message = "Total distance is required")
    @Min(value = 0, message = "Total distance cannot be negative")
    private Double totalDistance;

    @NotNull(message = "Distance per min is required")
    @Min(value = 0, message = "Distance per min cannot be negative")
    private Double distancePerMin;

    @NotNull(message = "High intensity distance is required")
    @Min(value = 0, message = "High intensity distance cannot be negative")
    private Double highIntensityDistance;

    @NotNull(message = "Top speed is required")
    @DecimalMin(value = "0.0", message = "Top speed cannot be negative")
    @DecimalMax(value = "45.0", message = "Top speed is unrealistic")
    private Double topSpeed;

    @NotNull(message = "Effort rating is required")
    @Min(value = 1, message = "Effort rating must be between 1 and 10")
    @Max(value = 10, message = "Effort rating must be between 1 and 10")
    private Integer effortRating;

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public Double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }

    public Double getDistancePerMin() { return distancePerMin; }
    public void setDistancePerMin(Double distancePerMin) { this.distancePerMin = distancePerMin; }

    public Double getHighIntensityDistance() { return highIntensityDistance; }
    public void setHighIntensityDistance(Double highIntensityDistance) { this.highIntensityDistance = highIntensityDistance; }

    public Double getTopSpeed() { return topSpeed; }
    public void setTopSpeed(Double topSpeed) { this.topSpeed = topSpeed; }

    public Integer getEffortRating() { return effortRating; }
    public void setEffortRating(Integer effortRating) { this.effortRating = effortRating; }
}