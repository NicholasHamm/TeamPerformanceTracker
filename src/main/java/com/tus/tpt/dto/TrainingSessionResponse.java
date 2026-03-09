package com.tus.tpt.dto;

import com.tus.tpt.model.TrainingType;

import java.time.LocalDateTime;
import java.util.Set;

public record TrainingSessionResponse(
        Long id,
        LocalDateTime datetime,
        TrainingType type,
        long duration,
        Set<PlayerDto> players
) {}