package com.maatram.controller;

import com.maatram.dto.AnnouncementRequest;
import com.maatram.dto.AnnouncementResponse;
import com.maatram.entity.User;
import com.maatram.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    @GetMapping
    public List<AnnouncementResponse> published() {
        return announcementService.published();
    }

    @GetMapping("/admin")
    public List<AnnouncementResponse> all() {
        return announcementService.all();
    }

    @PostMapping("/admin")
    public AnnouncementResponse create(@Valid @RequestBody AnnouncementRequest request,
                                        @AuthenticationPrincipal User admin) {
        return announcementService.create(request, admin);
    }

    @PutMapping("/admin/{id}")
    public AnnouncementResponse update(@PathVariable Long id,
                                       @Valid @RequestBody AnnouncementRequest request,
                                       @AuthenticationPrincipal User admin) {
        return announcementService.update(id, request, admin);
    }

    @PatchMapping("/admin/{id}/publish")
    public AnnouncementResponse publish(@PathVariable Long id, @RequestParam boolean value) {
        return announcementService.setPublished(id, value);
    }

    @DeleteMapping("/admin/{id}")
    public void delete(@PathVariable Long id) {
        announcementService.delete(id);
    }
}
