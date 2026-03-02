package com.tus.tpt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tus.tpt.Exception.DuplicateUsernameException;
import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.dto.CreateNewUser;
import com.tus.tpt.dto.UserResponse;
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
	
    public boolean doesUsernameExist(String username) {
        return getUser(username).isPresent();
    }
    
    public UserResponse createUser(CreateNewUser create) {

        String username = validateNewUsername(create.getUsername());

        if (create.getPassword() == null || create.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
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
//        user.setPassword(create.getPassword());
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

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        username = username.trim();
        if (userRepo.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateUsernameException(username);
        }
        
        return username;
    }

}
