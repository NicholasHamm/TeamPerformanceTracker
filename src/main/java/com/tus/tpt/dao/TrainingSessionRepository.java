package com.tus.tpt.dao;

import com.tus.tpt.model.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingSessionRepository  extends JpaRepository<TrainingSession, Long> {
}
