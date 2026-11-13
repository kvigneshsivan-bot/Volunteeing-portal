package com.maatram.controller;

import com.maatram.dto.EventRequest;
import com.maatram.dto.EventResponse;
import com.maatram.entity.User;
import com.maatram.entity.EventStatus;
import com.maatram.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public List<EventResponse> approvedEvents() {
        return eventService.approvedEvents();
    }

    @PostMapping
    public EventResponse create(@Valid @RequestBody EventRequest request,
                                @AuthenticationPrincipal User organiser) {
        return eventService.create(request, organiser);
    }

    @GetMapping("/mine")
    public List<EventResponse> mine(@AuthenticationPrincipal User organiser) {
        return eventService.organiserEvents(organiser);
    }

    @PostMapping("/{eventId}/registrations")
    public EventResponse register(@PathVariable Long eventId,
                                  @AuthenticationPrincipal User volunteer) {
        return eventService.register(eventId, volunteer);
    }

    @GetMapping("/pending")
    public List<EventResponse> pending() {
        return eventService.pendingEvents();
    }

    @PutMapping("/{eventId}/status")
    public EventResponse updateStatus(@PathVariable Long eventId,
                                      @RequestParam EventStatus status) {
        return eventService.updateStatus(eventId, status);
    }
}
