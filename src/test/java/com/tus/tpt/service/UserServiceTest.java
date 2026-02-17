package com.tus.tpt.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.tus.tpt.Exception.DuplicateUsernameException;
import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.dto.CreateNewUser;
import com.tus.tpt.dto.Role;
import com.tus.tpt.model.User;

class UserServiceTest {
    private UserRepository userRepo;
    private UserService service;
    CreateNewUser dto;

    @BeforeEach
    void setUp(){
        userRepo = mock(UserRepository.class);
        service = new UserService(userRepo);
        
        dto = new CreateNewUser();
        dto.setUsername("Joe");
        dto.setPassword("Password1");
        dto.setFirstname("Joe");
        dto.setLastname("Bloggs");
        dto.setRole(Role.ADMIN);
    }
    
    @Test
    void createNewUserTestSuccess() {
        when(userRepo.existsByUsernameIgnoreCase("Joe")).thenReturn(false);
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = service.createUser(dto);

        assertEquals("Joe", saved.getUsername());
        assertEquals("Password1", saved.getPassword());
        assertEquals("Joe", saved.getFirstName());
        assertEquals("Bloggs", saved.getLastName());
        assertEquals(Role.ADMIN, saved.getRole());
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
                (Consumer<CreateNewUser>) dto -> dto.setFirstname(""),
                "First name is required"
            ),
            Arguments.of(
                (Consumer<CreateNewUser>) dto -> dto.setLastname(""),
                "Last name is required"
            ),
            Arguments.of(
                (Consumer<CreateNewUser>) dto -> dto.setRole(null),
                "Role is required"
            )
        );
    }

}
