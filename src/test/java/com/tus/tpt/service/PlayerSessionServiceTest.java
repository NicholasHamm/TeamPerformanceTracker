package com.tus.tpt.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tus.tpt.dao.PlayerPerformanceRepository;
import com.tus.tpt.dto.player.PlayerSessionResponse;
import com.tus.tpt.model.PlayerPerformance;
import com.tus.tpt.model.TrainingSession;
import com.tus.tpt.model.TrainingType;

class PlayerSessionServiceTest {

    private PlayerPerformanceRepository playerPerformanceRepository;
    private PlayerSessionService service;

    @BeforeEach
    void setUp() {
        playerPerformanceRepository = mock(PlayerPerformanceRepository.class);
        service = new PlayerSessionService(playerPerformanceRepository);
    }

    @Test
    void getSessionsForLoggedInPlayerTestSuccess() {
        String username = "player1";

        TrainingSession session1 = new TrainingSession();
        session1.setDatetime(LocalDateTime.of(2026, 3, 10, 18, 0));
        session1.setType(TrainingType.CONDITIONING);
        session1.setDuration(60);

        PlayerPerformance performance1 = new PlayerPerformance();
        performance1.setSession(session1);
        performance1.setTotalDistance(5000.0);
        performance1.setHighIntensityDistance(1200.0);
        performance1.setTopSpeed(28.5);
        performance1.setEffortRating(8);

        TrainingSession session2 = new TrainingSession();
        session2.setDatetime(LocalDateTime.of(2026, 3, 8, 19, 30));
        session2.setType(TrainingType.SPEED);
        session2.setDuration(75);

        PlayerPerformance performance2 = new PlayerPerformance();
        performance2.setSession(session2);
        performance2.setTotalDistance(6200.0);
        performance2.setHighIntensityDistance(1500.0);
        performance2.setTopSpeed(30.2);
        performance2.setEffortRating(9);

        when(playerPerformanceRepository.findByPlayer_UsernameIgnoreCaseOrderBySession_DatetimeDesc(username))
                .thenReturn(List.of(performance1, performance2));

        List<PlayerSessionResponse> result = service.getSessionsForLoggedInPlayer(username);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(LocalDateTime.of(2026, 3, 10, 18, 0), result.get(0).datetime());
        assertEquals(TrainingType.CONDITIONING, result.get(0).type());
        assertEquals(60, result.get(0).duration());
        assertEquals(5000.0, result.get(0).totalDistance());
        assertEquals(1200.0, result.get(0).highIntensityDistance());
        assertEquals(28.5, result.get(0).topSpeed());
        assertEquals(8, result.get(0).effortRating());

        assertEquals(LocalDateTime.of(2026, 3, 8, 19, 30), result.get(1).datetime());
        assertEquals(TrainingType.SPEED, result.get(1).type());
        assertEquals(75, result.get(1).duration());
        assertEquals(6200.0, result.get(1).totalDistance());
        assertEquals(1500.0, result.get(1).highIntensityDistance());
        assertEquals(30.2, result.get(1).topSpeed());
        assertEquals(9, result.get(1).effortRating());

        verify(playerPerformanceRepository, times(1))
                .findByPlayer_UsernameIgnoreCaseOrderBySession_DatetimeDesc(username);
    }

    @Test
    void getSessionsForLoggedInPlayerTestShouldReturnEmptyListWhenNoSessionsFound() {
        String username = "player1";

        when(playerPerformanceRepository.findByPlayer_UsernameIgnoreCaseOrderBySession_DatetimeDesc(username))
                .thenReturn(List.of());

        List<PlayerSessionResponse> result = service.getSessionsForLoggedInPlayer(username);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(playerPerformanceRepository, times(1))
                .findByPlayer_UsernameIgnoreCaseOrderBySession_DatetimeDesc(username);
    }

    @Test
    void getSessionsForLoggedInPlayerTestShouldThrowWhenUsernameIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getSessionsForLoggedInPlayer(null)
        );

        assertEquals("Username is required", ex.getMessage());
        verify(playerPerformanceRepository, never())
                .findByPlayer_UsernameIgnoreCaseOrderBySession_DatetimeDesc(anyString());
    }

    @Test
    void getSessionsForLoggedInPlayerTestShouldThrowWhenUsernameIsBlank() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getSessionsForLoggedInPlayer("   ")
        );

        assertEquals("Username is required", ex.getMessage());
        verify(playerPerformanceRepository, never())
                .findByPlayer_UsernameIgnoreCaseOrderBySession_DatetimeDesc(anyString());
    }
}