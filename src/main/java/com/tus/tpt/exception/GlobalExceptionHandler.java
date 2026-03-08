package com.tus.tpt.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleIllegal(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(Map.of(
	        "error", "VALIDATION_ERROR",
	        "message", ex.getMessage()));
	}
	
	@ExceptionHandler(DuplicateUsernameException.class)
	public ResponseEntity<Map<String, String>> handleDup(DuplicateUsernameException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
			"error", "DUPLICATE_USERNAME",
			"message", ex.getMessage()));
	}
}