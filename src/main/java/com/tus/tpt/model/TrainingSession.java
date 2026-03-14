package com.tus.tpt.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "training_session")
public class TrainingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime datetime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainingType type;

    @Column(nullable = false)
    private int duration;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlayerPerformance> performances = new HashSet<>();

    public TrainingSession(){}

    public TrainingSession(LocalDateTime date, TrainingType type, int duration) {
        this.datetime = date;
        this.type = type;
        this.duration = duration;
        this.performances = new HashSet<>();
    }

    public Long getId() { return id; }
    public LocalDateTime getDatetime() { return datetime; }
    public void setDatetime(LocalDateTime datetime) { this.datetime = datetime; }

    public TrainingType getType() { return type; }
    public void setType(TrainingType type) { this.type = type; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public Set<PlayerPerformance> getPerformances() { return performances; }
    public void setPerformances(Set<PlayerPerformance> performances) { this.performances = performances; }
}