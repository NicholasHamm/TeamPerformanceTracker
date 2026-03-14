package com.tus.tpt.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.stream.Stream;

import com.tus.tpt.dao.PlayerPerformanceRepository;
import com.tus.tpt.dao.TrainingSessionRepository;
import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.dto.upload.PlayerPerformanceResponse;
import com.tus.tpt.dto.upload.UploadPlayerPerformance;
import com.tus.tpt.model.PlayerPerformance;
import com.tus.tpt.model.Role;
import com.tus.tpt.model.TrainingSession;
import com.tus.tpt.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.dao.DataIntegrityViolationException;

class UploadPerformanceServiceTest {

    private PlayerPerformanceRepository performanceRepository;
    private UserRepository userRepository;
    private TrainingSessionRepository sessionRepository;
    private UploadPerformanceService service;

    private UploadPlayerPerformance request;
    private User player;
    private User coach;
    private TrainingSession session;

    @BeforeEach
    void setUp() {
        performanceRepository = mock(PlayerPerformanceRepository.class);
        userRepository = mock(UserRepository.class);
        sessionRepository = mock(TrainingSessionRepository.class);

        service = new UploadPerformanceService(
                performanceRepository,
                userRepository,
                sessionRepository
        );

        request = new UploadPlayerPerformance();
        request.setPlayerId(3L);
        request.setTotalDistance(6000.0);
        request.setHighIntensityDistance(800.0);
        request.setTopSpeed(12.2);
        request.setEffortRating(7);

        player = mock(User.class);
        when(player.getId()).thenReturn(3L);
        when(player.getFirstName()).thenReturn("Mike");
        when(player.getLastName()).thenReturn("Player");
        when(player.getRole()).thenReturn(Role.PLAYER);

        coach = mock(User.class);
        when(coach.getId()).thenReturn(9L);
        when(coach.getFirstName()).thenReturn("John");
        when(coach.getLastName()).thenReturn("Coach");
        when(coach.getRole()).thenReturn(Role.COACH);

        session = mock(TrainingSession.class);
        when(session.getId()).thenReturn(1L);
        when(session.getDuration()).thenReturn(60);
    }

    @Test
    void uploadPlayerDataSuccess() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(player));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(performanceRepository.existsByPlayerIdAndSessionId(3L, 1L)).thenReturn(false);
        when(performanceRepository.save(any(PlayerPerformance.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        PlayerPerformanceResponse result = service.uploadPlayerData(1L, request);

        assertNotNull(result);
        assertEquals(3L, result.playerId());
        assertEquals("Mike Player", result.playerName());
        assertEquals(6000.0, result.totalDistance());
        assertEquals(100.0, result.distancePerMin());
        assertEquals(800.0, result.highIntensityDistance());
        assertEquals(12.2, result.topSpeed());
        assertEquals(7, result.effortRating());

        verify(userRepository).findById(3L);
        verify(sessionRepository).findById(1L);
        verify(performanceRepository).existsByPlayerIdAndSessionId(3L, 1L);
        verify(performanceRepository).save(any(PlayerPerformance.class));
    }

    @Test
    void uploadPlayerDataPlayerNotFound() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.uploadPlayerData(1L, request)
        );

        assertEquals("Player not found", ex.getMessage());
        verify(userRepository).findById(3L);
        verify(sessionRepository, never()).findById(anyLong());
        verify(performanceRepository, never()).save(any());
    }

    @Test
    void uploadPlayerDataUserIsNotPlayer() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(coach));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.uploadPlayerData(1L, request)
        );

        assertEquals("User is not a player", ex.getMessage());
        verify(userRepository).findById(3L);
        verify(sessionRepository, never()).findById(anyLong());
        verify(performanceRepository, never()).save(any());
    }

    @Test
    void uploadPlayerDataSessionNotFound() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(player));
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.uploadPlayerData(1L, request)
        );

        assertEquals("Training session not found", ex.getMessage());
        verify(userRepository).findById(3L);
        verify(sessionRepository).findById(1L);
        verify(performanceRepository, never()).existsByPlayerIdAndSessionId(anyLong(), anyLong());
        verify(performanceRepository, never()).save(any());
    }

    @Test
    void uploadPlayerDataDuplicateExistsBeforeSave() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(player));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(performanceRepository.existsByPlayerIdAndSessionId(3L, 1L)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.uploadPlayerData(1L, request)
        );

        assertEquals("Data already exists for this player in this session", ex.getMessage());
        verify(performanceRepository).existsByPlayerIdAndSessionId(3L, 1L);
        verify(performanceRepository, never()).save(any());
    }

    @Test
    void uploadPlayerDataHighIntensityDistanceGreaterThanTotalDistance() {
        request.setTotalDistance(500.0);
        request.setHighIntensityDistance(800.0);

        when(userRepository.findById(3L)).thenReturn(Optional.of(player));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(performanceRepository.existsByPlayerIdAndSessionId(3L, 1L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.uploadPlayerData(1L, request)
        );

        assertEquals("High intensity distance cannot exceed total distance", ex.getMessage());
        verify(performanceRepository, never()).save(any());
    }

    @Test
    void uploadPlayerDataDuplicateDetectedOnSave() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(player));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(performanceRepository.existsByPlayerIdAndSessionId(3L, 1L)).thenReturn(false);
        when(performanceRepository.save(any(PlayerPerformance.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.uploadPlayerData(1L, request)
        );

        assertEquals("Data already exists for this player in this session", ex.getMessage());
        verify(performanceRepository).save(any(PlayerPerformance.class));
    }

    @ParameterizedTest
    @MethodSource("distancePerMinuteExamples")
    void uploadPlayerDataCalculatesDistancePerMinuteCorrectly(
            double totalDistance,
            int duration,
            double expectedDistancePerMinute
    ) {
        request.setTotalDistance(totalDistance);
        request.setHighIntensityDistance(Math.min(500.0, totalDistance));

        when(session.getDuration()).thenReturn(duration);
        when(userRepository.findById(3L)).thenReturn(Optional.of(player));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(performanceRepository.existsByPlayerIdAndSessionId(3L, 1L)).thenReturn(false);
        when(performanceRepository.save(any(PlayerPerformance.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        PlayerPerformanceResponse result = service.uploadPlayerData(1L, request);

        assertEquals(expectedDistancePerMinute, result.distancePerMin());
    }

    private static Stream<Arguments> distancePerMinuteExamples() {
        return Stream.of(
                Arguments.of(5000.0, 60, 83.33333333333333),
                Arguments.of(5100.0, 60, 85.0),
                Arguments.of(1000.0, 32, 31.25)
        );
    }
}