package com.tus.tpt.dto.player;

import com.tus.tpt.model.TrainingType;
import java.time.LocalDateTime;

public class PlayerSessionResponse {
    private Long sessionId;
    private LocalDateTime datetime;
    private TrainingType type;
    private Integer duration;
    private Double totalDistance;
    private Double highIntensityDistance;
    private Double topSpeed;
    private Integer effortRating;

    public PlayerSessionResponse(Long sessionId,
                                 LocalDateTime datetime,
                                 TrainingType type,
                                 Integer duration,
                                 Double totalDistance,
                                 Double highIntensityDistance,
                                 Double topSpeed,
                                 Integer effortRating) {
        this.sessionId = sessionId;
        this.datetime = datetime;
        this.type = type;
        this.duration = duration;
        this.totalDistance = totalDistance;
        this.highIntensityDistance = highIntensityDistance;
        this.topSpeed = topSpeed;
        this.effortRating = effortRating;
    }

    public Long getSessionId() {
        return sessionId;
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

    public Double getTopSpeed() {
        return topSpeed;
    }

    public Integer getEffortRating() {
        return effortRating;
    }
}