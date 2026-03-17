package com.tus.tpt.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.tus.tpt.dao.PlayerPerformanceRepository;
import com.tus.tpt.dao.TrainingSessionRepository;
import com.tus.tpt.dto.analytics.PlayerMetricResponse;
import com.tus.tpt.dto.analytics.SessionAveragesResponse;
import com.tus.tpt.model.PlayerPerformance;
import com.tus.tpt.model.TrainingSession;
import com.tus.tpt.model.User;

class SessionAnalyticsServiceTest {

    private TrainingSessionRepository trainingSessionRepository;
    private PlayerPerformanceRepository playerPerformanceRepository;
    private SessionAnalyticsService service;

    private Long sessionId;
    private TrainingSession session;

    private User player1;
    private User player2;

    private PlayerPerformance performance1;
    private PlayerPerformance performance2;

    @BeforeEach
    void setUp() {
        trainingSessionRepository = mock(TrainingSessionRepository.class);
        playerPerformanceRepository = mock(PlayerPerformanceRepository.class);
        service = new SessionAnalyticsService(trainingSessionRepository, playerPerformanceRepository);

        sessionId = 1L;

        session = mock(TrainingSession.class);
        when(session.getId()).thenReturn(sessionId);

        player1 = new User();
        player1.setFirstName("Player");
        player1.setLastName("One");

        player2 = new User();
        player2.setFirstName("Player");
        player2.setLastName("Two");

        performance1 = new PlayerPerformance();
        performance1.setPlayer(player1);
        performance1.setTotalDistance(5000.0);
        performance1.setDistancePerMin(83.3);
        performance1.setHighIntensityDistance(1200.0);
        performance1.setTopSpeed(28.0);
        performance1.setEffortRating(8);

        performance2 = new PlayerPerformance();
        performance2.setPlayer(player2);
        performance2.setTotalDistance(7000.0);
        performance2.setDistancePerMin(93.3);
        performance2.setHighIntensityDistance(1800.0);
        performance2.setTopSpeed(32.0);
        performance2.setEffortRating(10);
    }

    @Test
    void getSessionAveragesTestSuccess() {
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(playerPerformanceRepository.findBySessionId(sessionId)).thenReturn(List.of(performance1, performance2));

        SessionAveragesResponse result = service.getSessionAverages(sessionId);

        assertNotNull(result);
        assertEquals(sessionId, result.sessionId());
        assertEquals(6000.0, result.averageTotalDistance());
        assertEquals(88.3, result.averageDistancePerMin(), 0.0001);
        assertEquals(1500.0, result.averageHighIntensityDistance());
        assertEquals(30.0, result.averageTopSpeed());
        assertEquals(9.0, result.averageEffortRating());

        verify(trainingSessionRepository, times(1)).findById(sessionId);
        verify(playerPerformanceRepository, times(1)).findBySessionId(sessionId);
    }

    @Test
    void getSessionAveragesTestSessionNotFound() {
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getSessionAverages(sessionId)
        );

        assertEquals("Training session not found", ex.getMessage());
        verify(trainingSessionRepository, times(1)).findById(sessionId);
        verify(playerPerformanceRepository, never()).findBySessionId(anyLong());
    }

    @Test
    void getSessionAveragesTestNoPerformanceData() {
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(playerPerformanceRepository.findBySessionId(sessionId)).thenReturn(List.of());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getSessionAverages(sessionId)
        );

        assertEquals("No player performance data found for this session", ex.getMessage());
        verify(trainingSessionRepository, times(1)).findById(anyLong());
        verify(playerPerformanceRepository, times(1)).findBySessionId(anyLong());
    }

    @Test
    void getPlayerMetricBreakdownTestTotalDistanceSuccess() {
        performance2.setTotalDistance(6200.0);
        performance2.setDistancePerMin(88.0);
        performance2.setHighIntensityDistance(1500.0);
        performance2.setTopSpeed(30.0);
        performance2.setEffortRating(9);

        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(playerPerformanceRepository.findBySessionId(sessionId))
                .thenReturn(List.of(performance1, performance2));

        List<PlayerMetricResponse> result = service.getPlayerMetricBreakdown(sessionId, "totalDistance");

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Player One", result.get(0).playerName());
        assertEquals(5000.0, result.get(0).value());

        assertEquals("Player Two", result.get(1).playerName());
        assertEquals(6200.0, result.get(1).value());

        verify(trainingSessionRepository, times(1)).findById(sessionId);
        verify(playerPerformanceRepository, times(1)).findBySessionId(sessionId);
    }

    @ParameterizedTest
    @MethodSource("validMetricInputs")
    void getPlayerMetricBreakdownValidMetrics(Consumer<PlayerPerformance> performanceSetter,
                                              String metric,
                                              Double expectedValue) {
        PlayerPerformance performance = new PlayerPerformance();
        performance.setPlayer(player1);
        performanceSetter.accept(performance);

        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(playerPerformanceRepository.findBySessionId(sessionId)).thenReturn(List.of(performance));

        List<PlayerMetricResponse> result = service.getPlayerMetricBreakdown(sessionId, metric);

        assertEquals(1, result.size());
        assertEquals("Player One", result.get(0).playerName());
        assertEquals(expectedValue, result.get(0).value());

        verify(trainingSessionRepository).findById(sessionId);
        verify(playerPerformanceRepository).findBySessionId(sessionId);
    }

    @Test
    void getPlayerMetricBreakdownTestSessionNotFound() {
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getPlayerMetricBreakdown(sessionId, "totalDistance")
        );

        assertEquals("Training session not found", ex.getMessage());
        verify(trainingSessionRepository, times(1)).findById(sessionId);
        verify(playerPerformanceRepository, never()).findBySessionId(anyLong());
    }

    @Test
    void getPlayerMetricBreakdownTestNoPerformanceData() {
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(playerPerformanceRepository.findBySessionId(sessionId)).thenReturn(List.of());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getPlayerMetricBreakdown(sessionId, "totalDistance")
        );

        assertEquals("No player performance data found for this session", ex.getMessage());
        verify(trainingSessionRepository, times(1)).findById(anyLong());
        verify(playerPerformanceRepository, times(1)).findBySessionId(anyLong());
    }

    @Test
    void getPlayerMetricBreakdownTestInvalidMetric() {
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(playerPerformanceRepository.findBySessionId(sessionId)).thenReturn(List.of(performance1));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getPlayerMetricBreakdown(sessionId, "invalidMetric")
        );

        assertEquals("Invalid metric selected", ex.getMessage());
        verify(trainingSessionRepository, times(1)).findById(anyLong());
        verify(playerPerformanceRepository, times(1)).findBySessionId(anyLong());
    }

    private static Stream<Arguments> validMetricInputs() {
        return Stream.of(
                Arguments.of(
                        (Consumer<PlayerPerformance>) p -> p.setTotalDistance(5000.0),
                        "totalDistance",
                        5000.0
                ),
                Arguments.of(
                        (Consumer<PlayerPerformance>) p -> p.setDistancePerMin(83.3),
                        "distancePerMin",
                        83.3
                ),
                Arguments.of(
                        (Consumer<PlayerPerformance>) p -> p.setHighIntensityDistance(1200.0),
                        "highIntensityDistance",
                        1200.0
                ),
                Arguments.of(
                        (Consumer<PlayerPerformance>) p -> p.setTopSpeed(29.1),
                        "topSpeed",
                        29.1
                ),
                Arguments.of(
                        (Consumer<PlayerPerformance>) p -> p.setEffortRating(8),
                        "effortRating",
                        8.0
                )
        );
    }
}