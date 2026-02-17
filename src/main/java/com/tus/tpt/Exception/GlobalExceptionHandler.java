package com.tus.tpt.Exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DuplicateUsernameException.class)
  public ResponseEntity<?> handleDup(DuplicateUsernameException ex) {
    return ResponseEntity.status(409).body(Map.of(
      "error", "DUPLICATE_USERNAME",
      "message", ex.getMessage()
    ));
  }
}