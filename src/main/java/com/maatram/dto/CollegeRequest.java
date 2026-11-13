package com.maatram.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CollegeRequest(@NotBlank String name, @Min(1) int capacity, long zoneId) {}
