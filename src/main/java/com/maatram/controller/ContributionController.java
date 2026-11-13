package com.maatram.controller;

import com.maatram.dto.AchievementResponse;
import com.maatram.dto.ContributionRequest;
import com.maatram.dto.ContributionResponse;
import com.maatram.entity.SubmissionStatus;
import com.maatram.entity.User;
import com.maatram.service.ContributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contributions")
@RequiredArgsConstructor
public class ContributionController {
    private final ContributionService contributionService;

    @PostMapping
    public ContributionResponse create(@Valid @RequestBody ContributionRequest request,
                                        @AuthenticationPrincipal User volunteer) {
        return contributionService.create(request, volunteer);
    }

    @GetMapping("/mine")
    public List<ContributionResponse> mine(@AuthenticationPrincipal User volunteer) {
        return contributionService.mine(volunteer);
    }

    @GetMapping("/mine/achievement")
    public AchievementResponse achievement(@AuthenticationPrincipal User volunteer) {
        return contributionService.achievement(volunteer);
    }

    @PatchMapping("/{id}/status")
    public ContributionResponse updateStatus(@PathVariable Long id,
                                              @RequestParam SubmissionStatus status) {
        return contributionService.updateStatus(id, status);
    }
}
