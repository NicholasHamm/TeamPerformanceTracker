package com.tus.tpt.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tus.tpt.service.UserService;

import com.tus.tpt.dto.CreateNewUser;
import com.tus.tpt.model.User;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	
	private final UserService userService;
	
	public AdminController(UserService userService) {
        this.userService = userService;
	}
	
    @GetMapping
    public List<User> getAllUsers() {
		return this.userService.findAllUsers();
    }
	
    @GetMapping("/{username}")
    public Optional<User> getUser(@PathVariable String username) {
		return this.userService.getUser(username);
    }
	
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateNewUser user) {
    	 User savedUser = userService.createUser(user);
         return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}
