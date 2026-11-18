package com.maatram.dto;

import com.maatram.entity.Role;
import jakarta.validation.constraints.NotNull;

public record AdminRoleRequest(@NotNull Role role) {}
