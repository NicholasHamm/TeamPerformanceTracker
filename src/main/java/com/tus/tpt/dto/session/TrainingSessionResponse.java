package com.tus.tpt.dto.session;

import com.tus.tpt.dto.player.PlayerDto;
import com.tus.tpt.model.TrainingType;

import java.time.LocalDateTime;
import java.util.Set;

public record TrainingSessionResponse(
        Long id,
        LocalDateTime datetime,
        TrainingType type,
        int duration,
        Set<PlayerDto> players
) {}