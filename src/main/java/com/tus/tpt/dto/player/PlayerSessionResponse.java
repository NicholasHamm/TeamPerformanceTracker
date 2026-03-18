package com.tus.tpt.dto.player;

import com.tus.tpt.model.TrainingType;
import java.time.LocalDateTime;

public record PlayerSessionResponse(LocalDateTime datetime,
                                 TrainingType type,
                                 Integer duration,
                                 Double totalDistance,
                                 Double highIntensityDistance,
                                 Double distancePerMin,
                                 Double topSpeed,
                                 Integer effortRating) {}