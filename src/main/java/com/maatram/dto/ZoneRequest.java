package com.maatram.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ZoneRequest(
        @NotBlank String name,
        @Min(1) int maxColleges,
        @Min(1) int maxStudents
) {}
