package com.maatram.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record ContributionRequest(
        @NotBlank @Size(max = 180) String eventName,
        @NotBlank @Size(max = 80) String activityType,
        @NotBlank @Size(max = 4000) String description,
        @NotNull @Size(min = 3, max = 20) List<@NotBlank @Size(max = 1000) String> proofPhotos,
        @NotNull LocalDate activityDate
) {}
