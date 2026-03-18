package com.tus.tpt.dto.player;

import com.tus.tpt.model.TrainingType;
import java.time.LocalDateTime;

public class PlayerSessionResponse {
    private LocalDateTime datetime;
    private TrainingType type;
    private Integer duration;
    private Double totalDistance;
    private Double highIntensityDistance;
    private Double distancePerMin;
    private Double topSpeed;
    private Integer effortRating;

    public PlayerSessionResponse(LocalDateTime datetime,
                                 TrainingType type,
                                 Integer duration,
                                 Double totalDistance,
                                 Double highIntensityDistance,
                                 Double distancePerMin,
                                 Double topSpeed,
                                 Integer effortRating) {
        this.datetime = datetime;
        this.type = type;
        this.duration = duration;
        this.totalDistance = totalDistance;
        this.highIntensityDistance = highIntensityDistance;
        this.distancePerMin = distancePerMin;
        this.topSpeed = topSpeed;
        this.effortRating = effortRating;
    }
    public LocalDateTime getDatetime() {
        return datetime;
    }

    public TrainingType getType() {
        return type;
    }

    public Integer getDuration() {
        return duration;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public Double getHighIntensityDistance() {
        return highIntensityDistance;
    }
    
    public Double getDistancePerMin() {
        return distancePerMin;
    }

    public Double getTopSpeed() {
        return topSpeed;
    }

    public Integer getEffortRating() {
        return effortRating;
    }
}