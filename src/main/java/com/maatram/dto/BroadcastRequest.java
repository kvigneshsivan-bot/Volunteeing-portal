package com.maatram.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BroadcastRequest(@NotBlank @Size(max = 1600) String message) {}
