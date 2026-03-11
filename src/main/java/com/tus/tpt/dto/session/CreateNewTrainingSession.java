package com.tus.tpt.dto.session;

public record CreateNewTrainingSession(
        String datetime,
        String type,
        int duration
) {}