package com.tus.tpt.dto.session;

import com.tus.tpt.dto.upload.PlayerPerformanceResponse;
import java.util.List;

public record SessionPerformanceResponse (
        Long sessionId,
        List<PlayerPerformanceResponse> performances)
{}