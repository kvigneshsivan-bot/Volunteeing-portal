package com.maatram.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrganisationRegisterRequest(
        @NotBlank @Size(max = 180) String organisationName,
        @NotBlank @Size(max = 1000) String description,
        @NotBlank @Size(max = 160) String contactName,
        @NotBlank @Email @Size(max = 320) String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 500) String verificationDocumentPath
) {}
