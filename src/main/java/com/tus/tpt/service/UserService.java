package com.tus.tpt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.tus.tpt.Exception.DuplicateUsernameException;
import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.dto.CreateNewUser;
import com.tus.tpt.model.User;

@Service
public class UserService {
	private final UserRepository userRepo;

	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	public List<User> findAllUsers(){
		return userRepo.findAll();
	}
	
    public Optional<User> getUser(String username) {
        return userRepo.findByUsernameIgnoreCase(username);
    }
    public boolean doesUsernameExist(String username) {
        return getUser(username).isPresent();
    }
    
    public User createUser(CreateNewUser create) {

        String username = create.getUsername() == null ? null : create.getUsername().trim();
        username = validateNewUsername(username);

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
//        user.setPassword(passwordEncoder.encode(create.getPassword()));
        user.setPassword(create.getPassword());
        user.setFirstName(create.getFirstName().trim());
        user.setLastName(create.getLastName().trim());
        user.setRole(create.getRole());

        return userRepo.save(user);
    }
    
    public String validateNewUsername(String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (userRepo.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateUsernameException(username);
        }
        
        return username;
    }

}
