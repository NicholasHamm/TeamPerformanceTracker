package com.tus.tpt.dao;

import com.tus.tpt.model.PlayerPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerPerformanceRepository extends JpaRepository<PlayerPerformance, Long> {

    List<PlayerPerformance> findBySessionId(Long sessionId);

    boolean existsByPlayerIdAndSessionId(Long playerId, Long sessionId);

    @Query("select pp.player.id from PlayerPerformance pp where pp.session.id = :sessionId")
    List<Long> findPlayerIdsBySessionId(@Param("sessionId") Long sessionId);
}