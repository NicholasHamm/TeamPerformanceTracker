package com.tus.tpt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tus.tpt.dto.analytics.*;
import com.tus.tpt.service.SessionAnalyticsService;

@RestController
@RequestMapping("/api/sessions")
public class SessionAnalyticsController {
	
    private final SessionAnalyticsService sessionAnalyticsService;

    public SessionAnalyticsController(SessionAnalyticsService sessionAnalyticsService) {
        this.sessionAnalyticsService = sessionAnalyticsService;
    }
    
	@GetMapping("/{sessionId}/averages")
	public ResponseEntity<SessionAveragesResponse> getSessionAverages(@PathVariable Long sessionId) {
	    return ResponseEntity.ok(sessionAnalyticsService.getSessionAverages(sessionId));
	}

	@GetMapping("/{sessionId}/metrics/{metric}")
	public ResponseEntity<List<PlayerMetricResponse>> getPlayerMetricBreakdown(
	        @PathVariable Long sessionId,
	        @PathVariable String metric) {
	    return ResponseEntity.ok(sessionAnalyticsService.getPlayerMetricBreakdown(sessionId, metric));
	}

}
