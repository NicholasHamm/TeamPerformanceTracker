package com.tus.tpt.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tus.tpt.Exception.DuplicateUsernameException;
import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.dto.CreateNewUser;
import com.tus.tpt.dto.UserResponse;
import com.tus.tpt.model.Role;
import com.tus.tpt.model.User;

class UserServiceTest {
    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;
    private UserService service;
    CreateNewUser dto;

    @BeforeEach
    void setUp(){
        userRepo = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new UserService(userRepo, passwordEncoder);
        
        dto = new CreateNewUser();
        dto.setUsername("Joe");
        dto.setPassword("Password1");
        dto.setFirstName("Joe");
        dto.setLastName("Bloggs");
        dto.setRole(Role.ADMIN);
    }
    
    @Test
    void createNewUserTestSuccess() {
        when(userRepo.existsByUsernameIgnoreCase("Joe")).thenReturn(false);
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse saved = service.createUser(dto);

        assertEquals("Joe", saved.username());
        //assertEquals("Password1", saved.password());
        assertEquals("Joe", saved.firstName());
        assertEquals("Bloggs", saved.lastName());
        assertEquals(Role.ADMIN, saved.role());
        verify(userRepo, times(1)).existsByUsernameIgnoreCase("Joe");
        verify(userRepo).save(any(User.class));
    }
    
    @Test
    void createNewUserTestDuplicateUsername() {
    	String dupUsername = "Joe";

    	when(userRepo.existsByUsernameIgnoreCase("Joe")).thenReturn(true);
        Exception e = assertThrows(
        		DuplicateUsernameException.class, () -> service.createUser(dto)
        );

        assertEquals("Username ["+dupUsername+"] already exists", e.getMessage());
        verify(userRepo, never()).save(any());
    }
    
    @ParameterizedTest
    @MethodSource("blankFieldInputs")
    void createNewUserBlankInputs(Consumer<CreateNewUser> dtoBlank, String expectedMessage) {
        dtoBlank.accept(dto);
        
	    IllegalArgumentException ex =
	            assertThrows(IllegalArgumentException.class,
	                    () -> service.createUser(dto));
	
	    assertEquals(expectedMessage, ex.getMessage());
	}
    
    private static Stream<Arguments> blankFieldInputs() {
        return Stream.of(
            Arguments.of(
                (Consumer<CreateNewUser>) dto -> dto.setUsername(""),
                "Username is required"
            ),
            Arguments.of(
                (Consumer<CreateNewUser>) dto -> dto.setPassword(""),
                "Password is required"
            ),
            Arguments.of(
                (Consumer<CreateNewUser>) dto -> dto.setFirstName(""),
                "First name is required"
            ),
            Arguments.of(
                (Consumer<CreateNewUser>) dto -> dto.setLastName(""),
                "Last name is required"
            ),
            Arguments.of(
                (Consumer<CreateNewUser>) dto -> dto.setRole(null),
                "Role is required"
            )
        );
    }

}
