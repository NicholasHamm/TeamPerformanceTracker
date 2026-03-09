package com.tus.tpt.dao;

import com.tus.tpt.model.TrainingSession;
import com.tus.tpt.model.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TrainingSessionRepository  extends JpaRepository<TrainingSession, Long> {

    boolean existsByDatetimeAndType(LocalDateTime datetime, TrainingType type);
}
