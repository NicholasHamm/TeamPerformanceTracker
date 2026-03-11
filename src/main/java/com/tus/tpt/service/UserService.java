package com.tus.tpt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tus.tpt.exception.DuplicateUsernameException;
import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.dto.user.CreateNewUser;
import com.tus.tpt.dto.user.UserResponse;
import com.tus.tpt.model.User;

@Service
public class UserService {
	private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }
	
	public List<UserResponse> findAllUsers(){
		return userRepo.findAll()
	            .stream()
	            .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getFirstName(), u.getLastName(), u.getRole()))
	            .toList();
	}
	
	public Optional<User> getUser(String username) {
	    if (username == null) return Optional.empty();
	    return userRepo.findByUsernameIgnoreCase(username.trim());
	}
    
    public UserResponse createUser(CreateNewUser create) {

        String username = validateNewUsername(create.getUsername());

        validateNewPassword(create.getPassword());

        if (create.getFirstName() == null || create.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (create.getLastName() == null || create.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (create.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(create.getPassword()));
        user.setFirstName(create.getFirstName().trim());
        user.setLastName(create.getLastName().trim());
        user.setRole(create.getRole());

        User saved = userRepo.save(user);
        return new UserResponse(saved.getId(), 
        		saved.getUsername(), 
        		saved.getFirstName(), 
        		saved.getLastName(), 
        		saved.getRole());
    }
    
    public String validateNewUsername(String username) {

        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
        username = username.trim();
        if (userRepo.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateUsernameException(username);
        }
        
        return username;
    }

    public String validateNewPassword(String password) {

        if (password == null || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters and include uppercase, lowercase and a number"
            );
        }

        return password;
    }

}
