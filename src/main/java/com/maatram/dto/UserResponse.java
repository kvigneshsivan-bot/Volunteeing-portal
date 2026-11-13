package com.maatram.dto;

import com.maatram.entity.User;

public record UserResponse(Long id, String fullName, String email, String volunteerId, String role, String accountStatus, int gratitudePoints) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getVolunteerId(),
                user.getRole().name(), user.getAccountStatus().name(), user.getGratitudePoints());
    }
}
