package com.maatram.controller;

import com.maatram.dto.OrganisationRegisterRequest;
import com.maatram.dto.OrganisationResponse;
import com.maatram.entity.Organisation;
import com.maatram.service.OrganisationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/organisations")
@RequiredArgsConstructor
public class OrganisationController {
    private final OrganisationService organisationService;

    @PostMapping("/register")
    public OrganisationResponse register(@Valid @RequestBody OrganisationRegisterRequest request) {
        return OrganisationResponse.from(organisationService.register(request));
    }
}
