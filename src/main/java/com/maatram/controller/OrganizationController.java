package com.maatram.controller;

import com.maatram.dto.CollegeRequest;
import com.maatram.dto.ZoneRequest;
import com.maatram.entity.College;
import com.maatram.entity.AccountStatus;
import com.maatram.entity.Organisation;
import com.maatram.dto.OrganisationResponse;
import com.maatram.entity.Zone;
import com.maatram.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    @GetMapping("/zones")
    public List<Zone> zones() {
        return organizationService.listZones();
    }

    @PostMapping("/zones")
    public Zone createZone(@Valid @RequestBody ZoneRequest request) {
        return organizationService.createZone(request);
    }

    @GetMapping("/colleges")
    public List<College> colleges() {
        return organizationService.listColleges();
    }

    @PostMapping("/colleges")
    public College createCollege(@Valid @RequestBody CollegeRequest request) {
        return organizationService.createCollege(request);
    }

    @GetMapping("/organisations/pending")
    public List<OrganisationResponse> pendingOrganisations() {
        return organizationService.pending().stream().map(OrganisationResponse::from).toList();
    }

    @PutMapping("/organisations/{organisationId}/status")
    public OrganisationResponse updateOrganisationStatus(@PathVariable Long organisationId,
                                                         @RequestParam AccountStatus status) {
        return OrganisationResponse.from(organizationService.updateStatus(organisationId, status));
    }
}
