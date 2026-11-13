package com.maatram.dto;

import com.maatram.entity.Organisation;

public record OrganisationResponse(Long id, String name, String description, String status, String ownerEmail) {
    public static OrganisationResponse from(Organisation organisation) {
        return new OrganisationResponse(
                organisation.getId(),
                organisation.getName(),
                organisation.getDescription(),
                organisation.getStatus().name(),
                organisation.getOwner().getEmail());
    }
}
