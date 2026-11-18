package com.maatram.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record AnnouncementRequest(
        @NotBlank @Size(max = 180) String title,
        @NotBlank @Size(max = 4000) String description,
        @NotNull LocalDate eventDate,
        @NotNull LocalTime eventTime,
        @NotBlank @Size(max = 250) String location,
        @Min(1) int requiredVolunteers,
        @Size(max = 1000) String skillsOrEligibility,
        @NotNull LocalDate registrationDeadline,
        @Size(max = 16777216) String imageUrl,
        @Size(max = 3000) String instructions
) {}
