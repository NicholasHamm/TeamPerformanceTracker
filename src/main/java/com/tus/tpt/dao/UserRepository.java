package com.tus.tpt.dao;

import java.util.List;
import java.util.Optional;

import com.tus.tpt.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tus.tpt.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByUsernameIgnoreCase(String username);
	Optional<User> findByUsernameIgnoreCase(String username);
    List<User> findByRole(Role role);
}
