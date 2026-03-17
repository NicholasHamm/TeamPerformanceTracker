package com.tus.tpt.dao;

import com.tus.tpt.model.PlayerPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PlayerPerformanceRepository extends JpaRepository<PlayerPerformance, Long> {

    List<PlayerPerformance> findBySessionId(Long sessionId);

    boolean existsByPlayerIdAndSessionId(Long playerId, Long sessionId);
    
    Optional<PlayerPerformance> findByPlayerIdAndSessionId(Long playerId, Long sessionId);

    List<PlayerPerformance> findByPlayer_UsernameIgnoreCaseOrderBySession_DatetimeDesc(String username);
    
    void deleteBySessionId(Long sessionId);
}