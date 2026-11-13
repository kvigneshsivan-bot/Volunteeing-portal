package com.maatram.dto;

import com.maatram.entity.EventCategory;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventRequest(
        @NotBlank @Size(max = 180) String title,
        @NotBlank @Size(max = 2000) String description,
        @NotNull EventCategory category,
        @NotNull Long zoneId,
        @NotNull LocalDate eventDate,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @NotBlank @Size(max = 250) String location,
        @Min(1) @Max(100000) int requiredVolunteers,
        @NotNull LocalDate registrationDeadline,
        @Size(max = 1000) String eligibilityCriteria,
        @Size(max = 2000) String instructions
) {}
