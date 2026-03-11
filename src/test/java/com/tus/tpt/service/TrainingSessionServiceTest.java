package com.tus.tpt.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.HashSet;
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
import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.dto.player.PlayerDto;
import com.tus.tpt.dto.session.TrainingSessionResponse;
import com.tus.tpt.model.Role;
import com.tus.tpt.model.TrainingSession;
import com.tus.tpt.model.TrainingType;
import com.tus.tpt.model.User;

class TrainingSessionServiceTest {

    private TrainingSessionRepository trainingSessionRepo;
    private UserRepository userRepo;
    private PlayerPerformanceRepository playerPerformanceRepo;
    private TrainingSessionService service;

    private TrainingSession session;
    private User player1;
    private User player2;
    private User coach;

    @BeforeEach
    void setUp() {
        trainingSessionRepo = mock(TrainingSessionRepository.class);
        userRepo = mock(UserRepository.class);
        playerPerformanceRepo = mock(PlayerPerformanceRepository.class);

        service = new TrainingSessionService(trainingSessionRepo, userRepo, playerPerformanceRepo);

        session = new TrainingSession();
        session.setDatetime(LocalDateTime.now().minusDays(1));
        session.setType(TrainingType.GYM);
        session.setDuration(60);
        session.setPlayers(new HashSet<>());

        player1 = new User();
        player1.setUsername("player1");
        player1.setFirstName("Mike");
        player1.setLastName("Player");
        player1.setRole(Role.PLAYER);

        player2 = new User();
        player2.setUsername("player2");
        player2.setFirstName("Tom");
        player2.setLastName("Jones");
        player2.setRole(Role.PLAYER);

        coach = new User();
        coach.setUsername("coach1");
        coach.setFirstName("John");
        coach.setLastName("Coach");
        coach.setRole(Role.COACH);
    }

    @Test
    void findAllTrainingSessionsSuccess() {
        session.getPlayers().add(player1);
        when(trainingSessionRepo.findAll()).thenReturn(List.of(session));

        List<TrainingSessionResponse> result = service.findAllTrainingSessions();

        assertEquals(1, result.size());
        assertNull(result.get(0).id());
        assertEquals(TrainingType.GYM, result.get(0).type());
        assertEquals(60, result.get(0).duration());
        assertEquals(1, result.get(0).players().size());
        verify(trainingSessionRepo, times(1)).findAll();
    }

    @Test
    void findTrainingSessionByIdSuccess() {
        when(trainingSessionRepo.findById(1L)).thenReturn(Optional.of(session));

        Optional<TrainingSessionResponse> result = service.findTrainingSessionById(1L);

        assertTrue(result.isPresent());
        assertNull(result.get().id());
        assertEquals(session.getDatetime(), result.get().datetime());
        assertEquals(session.getType(), result.get().type());
        verify(trainingSessionRepo).findById(1L);
    }

    @Test
    void findTrainingSessionByIdNullReturnsEmpty() {
        Optional<TrainingSessionResponse> result = service.findTrainingSessionById(null);

        assertTrue(result.isEmpty());
        verify(trainingSessionRepo, never()).findById(any());
    }

    @Test
    void findTrainingSessionByIdNotFoundReturnsEmpty() {
        when(trainingSessionRepo.findById(99L)).thenReturn(Optional.empty());

        Optional<TrainingSessionResponse> result = service.findTrainingSessionById(99L);

        assertTrue(result.isEmpty());
        verify(trainingSessionRepo).findById(99L);
    }

    @Test
    void createTrainingSessionSuccess() {
        when(trainingSessionRepo.findAll()).thenReturn(List.of());
        when(trainingSessionRepo.save(any(TrainingSession.class))).thenAnswer(inv -> inv.getArgument(0));

        TrainingSessionResponse saved = service.createTrainingSession(session);

        assertNull(saved.id());
        assertEquals(session.getDatetime(), saved.datetime());
        assertEquals(session.getType(), saved.type());
        assertEquals(session.getDuration(), saved.duration());
        verify(trainingSessionRepo).save(session);
    }

    @Test
    void getAvailablePlayersForSessionSuccess() {
        when(trainingSessionRepo.existsById(1L)).thenReturn(true);
        when(playerPerformanceRepo.findPlayerIdsBySessionId(1L)).thenReturn(List.of(10L));
        when(userRepo.findByRole(Role.PLAYER)).thenReturn(List.of(player1, player2));

        List<PlayerDto> result = service.getAvailablePlayersForSession(1L);

        assertEquals(2, result.size());
        assertEquals("Mike", result.get(0).firstName());
        assertEquals("Player", result.get(0).lastName());
        assertEquals("Tom", result.get(1).firstName());
        assertEquals("Jones", result.get(1).lastName());
    }

    @Test
    void getAvailablePlayersForSessionSessionNotFound() {
        when(trainingSessionRepo.existsById(1L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getAvailablePlayersForSession(1L)
        );

        assertEquals("Session not found", ex.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidTrainingSessionInputs")
    void validateTrainingSessionInvalidInputs(Consumer<TrainingSession> sessionChange, String expectedMessage) {
        sessionChange.accept(session);
        when(trainingSessionRepo.findAll()).thenReturn(List.of());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.validateTrainingSession(session)
        );

        assertEquals(expectedMessage, ex.getMessage());
    }

    private static Stream<Arguments> invalidTrainingSessionInputs() {
        return Stream.of(
                Arguments.of(
                        (Consumer<TrainingSession>) s -> s.setDatetime(null),
                        "Date & time are required"
                ),
                Arguments.of(
                        (Consumer<TrainingSession>) s -> s.setDatetime(LocalDateTime.now().plusDays(1)),
                        "Date cannot be in the future"
                ),
                Arguments.of(
                        (Consumer<TrainingSession>) s -> s.setType(null),
                        "Training type is required"
                ),
                Arguments.of(
                        (Consumer<TrainingSession>) s -> s.setDuration(10),
                        "Duration must be between 10 and 300 minutes"
                ),
                Arguments.of(
                        (Consumer<TrainingSession>) s -> s.setDuration(301),
                        "Duration must be between 10 and 300 minutes"
                )
        );
    }

    @Test
    void validateTrainingSessionNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.validateTrainingSession(null)
        );

        assertEquals("Training session is required", ex.getMessage());
    }

    @Test
    void validateTrainingSessionOverlap() {
        TrainingSession existing = new TrainingSession();
        existing.setDatetime(session.getDatetime().minusMinutes(30));
        existing.setType(TrainingType.PITCH);
        existing.setDuration(90);
        existing.setPlayers(new HashSet<>());

        when(trainingSessionRepo.findAll()).thenReturn(List.of(existing));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.validateTrainingSession(session)
        );

        assertEquals("Training session overlaps with an existing session", ex.getMessage());
    }

    @Test
    void validateTrainingSessionSuccess() {
        when(trainingSessionRepo.findAll()).thenReturn(List.of());

        assertDoesNotThrow(() -> service.validateTrainingSession(session));
    }

    @Test
    void isSessionAvailableTrueWhenOverlapExists() {
        TrainingSession existing = new TrainingSession();
        existing.setDatetime(session.getDatetime().minusMinutes(30));
        existing.setDuration(90);

        when(trainingSessionRepo.findAll()).thenReturn(List.of(existing));

        boolean available = service.isSessionAvailable(session);

        assertTrue(available);
    }

    @Test
    void isSessionAvailableFalseWhenNoOverlapExists() {
        TrainingSession existing = new TrainingSession();
        existing.setDatetime(session.getDatetime().minusHours(3));
        existing.setDuration(60);

        when(trainingSessionRepo.findAll()).thenReturn(List.of(existing));

        boolean available = service.isSessionAvailable(session);

        assertFalse(available);
    }
}