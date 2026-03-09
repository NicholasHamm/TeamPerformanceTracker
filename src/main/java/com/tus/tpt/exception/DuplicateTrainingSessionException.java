package com.tus.tpt.exception;

public class DuplicateTrainingSessionException extends RuntimeException {
    public DuplicateTrainingSessionException() {
        super("A training session already exists for this date/time and type");
    }
}
