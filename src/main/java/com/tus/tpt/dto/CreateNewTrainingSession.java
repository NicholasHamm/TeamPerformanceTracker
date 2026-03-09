package com.tus.tpt.dto;

public record CreateNewTrainingSession(
        String datetime,
        String type,
        long duration
) {}