package com.tus.tpt.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "player_performance",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_player_session", columnNames = {"player_id", "session_id"})
        }
)
public class PlayerPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private User player; // or Player entity if you have one

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private TrainingSession session;

    @Column(nullable = false)
    private Double totalDistance;

    @Column(nullable = false)
    private Double distancePerMin;

    @Column(nullable = false)
    private Double highIntensityDistance;

    @Column(nullable = false)
    private Double topSpeed;

    @Column(nullable = false)
    private Integer effortRating;

    public PlayerPerformance() {}

    public PlayerPerformance(User player, TrainingSession session, Double totalDistance,
                             Double distancePerMin, Double highIntensityDistance,
                             Double topSpeed, Integer effortRating) {
        this.player = player;
        this.session = session;
        this.totalDistance = totalDistance;
        this.distancePerMin = distancePerMin;
        this.highIntensityDistance = highIntensityDistance;
        this.topSpeed = topSpeed;
        this.effortRating = effortRating;
    }

    public Long getId() { return id; }
    public User getPlayer() { return player; }
    public void setPlayer(User player) { this.player = player; }
    public TrainingSession getSession() { return session; }
    public void setSession(TrainingSession session) { this.session = session; }
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
