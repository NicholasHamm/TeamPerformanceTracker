package com.tus.tpt.dto.upload;

import jakarta.validation.constraints.*;

public class UploadPlayerPerformance {

    @NotNull(message = "Player id is required")
    private Long playerId;

    @NotNull(message = "Total distance is required")
    @DecimalMin(value = "0.0", message = "Total distance cannot be negative")
    @DecimalMax(value = "20000.0", message = "Total distance is unrealistic (> 20,000m)")
    private Double totalDistance;

    @NotNull(message = "High intensity distance is required")
    @DecimalMin(value = "0.0", message = "High intensity distance cannot be negative")
    @DecimalMax(value = "10000.0", message = "High intensity distance is unrealistic (> 10,000m)")
    private Double highIntensityDistance;

    @NotNull(message = "Top speed is required")
    @DecimalMin(value = "0.0", message = "Top speed cannot be negative")
    @DecimalMax(value = "15.0", message = "Top speed is unrealistic (> 15m/s)")
    private Double topSpeed;

    @NotNull(message = "Effort rating is required")
    @Min(value = 1, message = "Effort rating must be between 1 and 10")
    @Max(value = 10, message = "Effort rating must be between 1 and 10")
    private Integer effortRating;

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public Double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }

    public Double getHighIntensityDistance() { return highIntensityDistance; }
    public void setHighIntensityDistance(Double highIntensityDistance) { this.highIntensityDistance = highIntensityDistance; }

    public Double getTopSpeed() { return topSpeed; }
    public void setTopSpeed(Double topSpeed) { this.topSpeed = topSpeed; }

    public Integer getEffortRating() { return effortRating; }
    public void setEffortRating(Integer effortRating) { this.effortRating = effortRating; }
}