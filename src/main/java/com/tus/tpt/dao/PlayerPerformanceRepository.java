package com.tus.tpt.dao;

import com.tus.tpt.dto.player.PlayerSessionResponse;
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

    @Query("""
        select new com.tus.tpt.dto.player.PlayerSessionResponse(
            ts.id,
            ts.datetime,
            ts.type,
            ts.duration,
            pp.totalDistance,
            pp.highIntensityDistance,
            pp.topSpeed,
            pp.effortRating
        )
        from PlayerPerformance pp
        join pp.session ts
        join pp.player p
        where upper(p.username) = upper(:username)
        order by ts.datetime desc
    """)
    List<PlayerSessionResponse> findSessionsForPlayerUsername(String username);
}