package com.tus.tpt.exception;

import java.time.format.DateTimeParseException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final String ERROR = "error";
	
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {

        FieldError error = ex.getBindingResult().getFieldErrors().getFirst();

        return ResponseEntity.badRequest().body(
                Map.of(ERROR, error.getDefaultMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of(ERROR, ex.getMessage()));
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Map<String, String>> handleDateTimeParse(DateTimeParseException ex) {
        return ResponseEntity.badRequest().body(Map.of(ERROR, "Invalid date/time format"));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateUsername(DuplicateUsernameException ex) {
        return ResponseEntity.badRequest().body(Map.of(ERROR, ex.getMessage()));
    }
}